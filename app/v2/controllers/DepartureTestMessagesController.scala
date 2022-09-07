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
import controllers.actions.AuthAction
import v2.controllers.actions.MessageRequestAction
import v2.controllers.actions.ValidateDepartureMessageTypeAction
import models.HateaosDepartureResponse
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.http.HeaderCarrier
import v2.services.DepartureService
import v2.services.InboundRouterService
import v2.services.MessageGenerationService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.ResponseHelper
import v2.models.request.MessageRequest
import v2.models.DepartureId
import v2.models.EORINumber
import v2.models.errors.PresentationError

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits._

@ImplementedBy(classOf[DepartureTestMessagesController])
trait V2DepartureTestMessagesController {
  def injectEISResponse(departureId: DepartureId): Action[JsValue]
}

class DepartureTestMessagesController @Inject()(cc: ControllerComponents,
                                                authAction: AuthAction,
                                                departureService: DepartureService,
                                                inboundRouterService: InboundRouterService,
                                                messageRequestAction: MessageRequestAction,
                                                validateDepartureMessageTypeAction: ValidateDepartureMessageTypeAction,
                                                msgGenService: MessageGenerationService)
    extends BackendController(cc)
    with ResponseHelper
    with ErrorTranslator
    with V2DepartureTestMessagesController {

  override def injectEISResponse(departureId: DepartureId): Action[JsValue] =
    (authAction andThen messageRequestAction andThen validateDepartureMessageTypeAction)
      .async(parse.json) {
        implicit request: MessageRequest[JsValue] =>
          implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

          (for {

            // Get matching departure from transit-movements
            _ <- (departureService.getDeparture(EORINumber(request.eori), departureId)).asPresentation

            message = msgGenService.generateMessage(request)
            // Send generated message to transit-movements-router
            messageId <- inboundRouterService.post(EORINumber(request.eori), request.messageType, message.toString(), departureId).asPresentation

            // Check for matching departure message from transit-movements
            _ <- departureService.getMessage(EORINumber(request.eori), departureId, messageId).asPresentation
          } yield (message, messageId)).fold(
            presentationError => Status(presentationError.code.statusCode)(Json.toJson(presentationError)),
            message =>
              Created(
                HateaosDepartureResponse(
                  departureId.value,
                  request.messageType.code,
                  message._1,
                  message._2.value
                )
            )
          )
      }

}
