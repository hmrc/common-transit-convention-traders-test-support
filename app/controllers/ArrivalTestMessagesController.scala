/*
 * Copyright 2022 HM Revenue & Customs
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

import connectors.InboundRouterConnector
import connectors.ArrivalConnector
import connectors.ArrivalMessageConnector
import controllers.actions.AuthAction
import controllers.actions.ChannelAction
import controllers.actions.MessageRequestAction
import controllers.actions.ValidateArrivalMessageTypeAction

import javax.inject.Inject
import models.ArrivalId
import models.HateaosArrivalResponse
import models.request.MessageRequest
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import services.MessageGenerationService
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.ResponseHelper
import utils.Utils

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ArrivalTestMessagesController @Inject()(cc: ControllerComponents,
                                              arrivalConnector: ArrivalConnector,
                                              inboundRouterConnector: InboundRouterConnector,
                                              arrivalMessageConnector: ArrivalMessageConnector,
                                              authAction: AuthAction,
                                              channelAction: ChannelAction,
                                              messageRequestAction: MessageRequestAction,
                                              validateArrivalMessageTypeAction: ValidateArrivalMessageTypeAction,
                                              msgGenService: MessageGenerationService)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with HttpErrorFunctions
    with ResponseHelper {

  def injectEISResponse(arrivalId: ArrivalId): Action[JsValue] =
    (authAction andThen channelAction andThen messageRequestAction andThen validateArrivalMessageTypeAction).async(parse.json) {
      implicit request: MessageRequest[JsValue] =>
        val message = msgGenService.generateMessage(request)

        arrivalConnector
          .get(arrivalId, request.channel)
          .flatMap {
            getResponse =>
              getResponse.status match {
                case status if is2xx(status) =>
                  inboundRouterConnector
                    .post(request.messageType, message.toString(), arrivalId.index)
                    .flatMap {
                      postResponse =>
                        postResponse.status match {
                          case status if is2xx(status) =>
                            postResponse.header(LOCATION) match {
                              case Some(locationValue) =>
                                val messageId = Utils.lastFragment(locationValue)
                                arrivalMessageConnector.get(arrivalId.index.toString, messageId, request.channel).map {
                                  case Right(_) =>
                                    Created(
                                      HateaosArrivalResponse(
                                        arrivalId,
                                        request.messageType,
                                        message,
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
                      case e =>
                        InternalServerError
                    }
                case _ =>
                  Future.successful(handleNon2xx(getResponse))
              }
          }
          .recover {
            case e =>
              InternalServerError
          }
    }
}
