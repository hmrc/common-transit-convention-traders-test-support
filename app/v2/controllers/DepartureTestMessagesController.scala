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

package v2.controllers

import com.google.inject.ImplementedBy
import v2.connectors.DepartureConnector
import v2.connectors.InboundRouterConnector
import v2.controllers.actions.AuthAction
import v2.controllers.actions.MessageRequestAction
import v2.controllers.actions.ValidateDepartureMessageTypeAction
import models.HateaosDepartureResponse
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import v2.services.MessageGenerationService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.ResponseHelper
import utils.Utils
import v2.models.request.MessageRequest
import v2.models.DepartureId
import v2.models.DepartureWithoutMessages

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@ImplementedBy(classOf[DepartureTestMessagesController])
trait V2DepartureTestMessagesController {
  def injectEISResponse(departureId: DepartureId): Action[JsValue]
}

class DepartureTestMessagesController @Inject()(cc: ControllerComponents,
                                                authAction: AuthAction,
                                                departureConnector: DepartureConnector,
                                                inboundRouterConnector: InboundRouterConnector,
                                                messageRequestAction: MessageRequestAction,
                                                validateDepartureMessageTypeAction: ValidateDepartureMessageTypeAction,
                                                msgGenService: MessageGenerationService)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with ResponseHelper
    with V2DepartureTestMessagesController {

  override def injectEISResponse(departureId: DepartureId): Action[JsValue] =
    (authAction andThen messageRequestAction andThen validateDepartureMessageTypeAction)
      .async(parse.json) {
        implicit request: MessageRequest[JsValue] =>
          val message = msgGenService.generateMessage(request)

          // Get matching departure from transit-movements
          departureConnector
            .getDeparture(request.eori, departureId)
            .flatMap {
              case Right(departureWithMessages: DepartureWithoutMessages) =>
                // Send generated message to transit-movements-router
                inboundRouterConnector
                  .post(request.eori, request.messageType, message.toString(), departureId.value)
                  .flatMap {
                    postResponse =>
                      postResponse.status match {
                        case status if is2xx(status) =>
                          postResponse.header(LOCATION) match {
                            case Some(locationValue) =>
                              val messageId = Utils.lastFragment(locationValue)

                              // Check for matching departure message from transit-movements
                              departureConnector.getMessage(request.eori, departureId.value, messageId).map {
                                case Right(_) =>
                                  Created(
                                    HateaosDepartureResponse(
                                      departureId.value,
                                      request.messageType.code,
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
