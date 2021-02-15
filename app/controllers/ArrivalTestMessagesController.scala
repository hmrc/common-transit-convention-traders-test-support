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

import connectors.InboundRouterConnector
import connectors.ArrivalConnector
import connectors.ArrivalMessageConnector
import controllers.actions.AuthAction
import controllers.actions.GeneratedMessageRequest
import controllers.actions.ValidateArrivalMessageTypeAction

import javax.inject.Inject
import models.ArrivalId
import models.HateaosArrivalResponse
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
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
                                              validateArrivalMessageTypeAction: ValidateArrivalMessageTypeAction)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with HttpErrorFunctions
    with ResponseHelper {

  def injectEISResponse(arrivalId: ArrivalId): Action[JsValue] =
    (authAction andThen validateArrivalMessageTypeAction).async(parse.json) {
      implicit request: GeneratedMessageRequest[JsValue] =>
        arrivalConnector
          .get(arrivalId)
          .flatMap {
            getResponse =>
              getResponse.status match {
                case status if is2xx(status) =>
                  inboundRouterConnector
                    .post(request.testMessage.messageType, request.generatedMessage.toString(), arrivalId.index)
                    .flatMap {
                      postResponse =>
                        postResponse.status match {
                          case status if is2xx(status) =>
                            postResponse.header(LOCATION) match {
                              case Some(locationValue) =>
                                val messageId = Utils.lastFragment(locationValue)
                                arrivalMessageConnector.get(arrivalId.index.toString, messageId).map {
                                  case Right(_) =>
                                    Created(
                                      HateaosArrivalResponse(
                                        arrivalId,
                                        request.testMessage.messageType,
                                        request.generatedMessage,
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
