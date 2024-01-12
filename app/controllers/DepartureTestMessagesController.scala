/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import com.google.inject.ImplementedBy
import com.google.inject.Inject
import com.google.inject.Singleton
import connectors.DepartureConnector
import connectors.DepartureMessageConnector
import connectors.InboundRouterConnector
import controllers.actions.AuthAction
import controllers.actions.ChannelAction
import controllers.actions.MessageRequestAction
import controllers.actions.ValidateDepartureMessageTypeAction
import controllers.actions.VersionOneEnabledCheckAction
import logging.Logging
import models.DepartureId
import models.DepartureWithMessages
import models.HateaosDepartureResponse
import models.MessageType
import models.request.MessageRequest
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import services.MessageGenerationService
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.ResponseHelper
import utils.Utils
import utils.XMLTransformer

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.xml.NodeSeq

@ImplementedBy(classOf[DepartureTestMessagesController])
trait V1DepartureTestMessagesController {
  def injectEISResponse(departureId: DepartureId): Action[JsValue]
  def submitDepartureDeclaration: Action[NodeSeq]
}

@Singleton
class DepartureTestMessagesController @Inject() (
  cc: ControllerComponents,
  departureConnector: DepartureConnector,
  inboundRouterConnector: InboundRouterConnector,
  departureMessageConnector: DepartureMessageConnector,
  versionOneEnabledCheckAction: VersionOneEnabledCheckAction,
  authAction: AuthAction,
  channelAction: ChannelAction,
  messageRequestAction: MessageRequestAction,
  validateDepartureMessageTypeAction: ValidateDepartureMessageTypeAction,
  msgGenService: MessageGenerationService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with V1DepartureTestMessagesController
    with HttpErrorFunctions
    with ResponseHelper
    with Logging {

  override def injectEISResponse(departureId: DepartureId): Action[JsValue] =
    (versionOneEnabledCheckAction andThen authAction andThen channelAction andThen messageRequestAction andThen validateDepartureMessageTypeAction)
      .async(parse.json) {
        implicit request: MessageRequest[JsValue] =>
          val message = msgGenService.generateMessage(request)

          departureConnector
            .getMessages(departureId, request.channel)
            .flatMap {
              case Right(departureWithMessages: DepartureWithMessages) =>
                val generatedMessage =
                  XMLTransformer.populateRefNumEPT1(message, MessageType.DepartureDeclaration.code, departureWithMessages.messages)
                inboundRouterConnector
                  .post(request.messageType, generatedMessage.toString(), departureId.index)
                  .flatMap {
                    postResponse =>
                      postResponse.status match {
                        case status if is2xx(status) =>
                          postResponse.header(LOCATION) match {
                            case Some(locationValue) =>
                              val messageId = Utils.lastFragment(locationValue)
                              departureMessageConnector.get(departureId.index.toString, messageId, request.channel).map {
                                case Right(_) =>
                                  Created(
                                    HateaosDepartureResponse(
                                      departureId,
                                      request.messageType,
                                      generatedMessage,
                                      locationValue
                                    )
                                  )
                                case Left(getMessageResponse) =>
                                  handleNon2xx(getMessageResponse)
                              }
                            case None =>
                              Future.successful(InternalServerError)
                          }
                        case _ =>
                          Future.successful(handleNon2xx(postResponse))
                      }
                  }
                  .recover {
                    case _ =>
                      InternalServerError
                  }
              case Left(getMessagesResponse) =>
                Future.successful(handleNon2xx(getMessagesResponse))
            }
            .recover {
              case _ =>
                InternalServerError
            }
      }

  override def submitDepartureDeclaration: Action[NodeSeq] = (versionOneEnabledCheckAction andThen authAction andThen channelAction).async(parse.xml) {
    implicit request =>
      departureConnector
        .createDeclarationMessage(request.body, request.channel)
        .map {
          response =>
            response.status match {
              case status if is2xx(status) =>
                response.header(LOCATION) match {
                  case Some(locationValue) =>
                    val departureId = Utils.lastFragment(locationValue)
                    Accepted(
                      Json.toJson(
                        HateaosDepartureResponse(
                          DepartureId(departureId.toInt),
                          MessageType.DepartureDeclaration,
                          request.body,
                          locationValue
                        )
                      )
                    )
                  case None =>
                    logger.error("Failed to submit departure declaration: 'LOCATION' is missing in response.header")
                    InternalServerError
                }
              case status =>
                logger.error(s"Failed to submit departure declaration: Received the status - $status")
                InternalServerError
            }
        }
  }

}
