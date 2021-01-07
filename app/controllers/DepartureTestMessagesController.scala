/*
 * Copyright 2021 HM Revenue & Customs
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

import connectors.DepartureConnector
import connectors.DepartureMessageConnector
import connectors.InboundRouterConnector
import controllers.actions.AuthAction
import controllers.actions.GeneratedMessageRequest
import controllers.actions.ValidateDepartureMessageTypeAction

import javax.inject.Inject
import models.DepartureId
import models.DepartureWithMessages
import models.HateaosDepartureResponse
import models.MessageType
import models.domain.MovementMessage
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import utils.ResponseHelper
import utils.Utils
import utils.XMLTransformer

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DepartureTestMessagesController @Inject()(cc: ControllerComponents,
                                                departureConnector: DepartureConnector,
                                                inboundRouterConnector: InboundRouterConnector,
                                                departureMessageConnector: DepartureMessageConnector,
                                                authAction: AuthAction,
                                                validateDepartureMessageTypeAction: ValidateDepartureMessageTypeAction)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with HttpErrorFunctions
    with ResponseHelper {

  def injectEISResponse(departureId: DepartureId): Action[JsValue] =
    (authAction andThen validateDepartureMessageTypeAction).async(parse.json) {
      implicit request: GeneratedMessageRequest[JsValue] =>
        departureConnector
          .getMessages(departureId)
          .flatMap {
            case Right(departureWithMessages: DepartureWithMessages) =>
              val generatedMessage =
                XMLTransformer.populateRefNumEPT1(request.generatedMessage, MessageType.DepartureDeclaration.code, departureWithMessages.messages)
              inboundRouterConnector
                .post(request.testMessage.messageType, generatedMessage.toString(), departureId.index)
                .flatMap {
                  postResponse =>
                    postResponse.status match {
                      case status if is2xx(status) =>
                        postResponse.header(LOCATION) match {
                          case Some(locationValue) =>
                            val messageId = Utils.lastFragment(locationValue)
                            departureMessageConnector.get(departureId.index.toString, messageId).map {
                              case Right(_) =>
                                Created(
                                  HateaosDepartureResponse(
                                    departureId,
                                    request.testMessage.messageType,
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
}
