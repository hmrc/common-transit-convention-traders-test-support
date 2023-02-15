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

package v2.controllers

import com.google.inject.ImplementedBy
import config.Constants
import v2.controllers.actions.AuthAction
import v2.controllers.actions.MessageRequestAction
import v2.models.CorrelationId
import v2.models.HateoasResponse
import v2.models.MessageId
import v2.models.MovementId
import v2.models.MovementType
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.http.HeaderCarrier
import v2.services.MovementPersistenceService
import v2.services.InboundRouterService
import v2.services.MessageGenerationService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.ResponseHelper
import v2.controllers.actions.ValidateMessageTypeActionProvider
import v2.models.request.MessageRequest

import javax.inject.Inject
import scala.concurrent.ExecutionContext

@ImplementedBy(classOf[TestMessagesController])
trait V2TestMessagesController {
  def sendDepartureResponse(departureId: MovementId): Action[JsValue]

  def sendArrivalsResponse(departureId: MovementId): Action[JsValue]
}

class TestMessagesController @Inject()(
  cc: ControllerComponents,
  authAction: AuthAction,
  movementPersistenceService: MovementPersistenceService,
  inboundRouterService: InboundRouterService,
  messageRequestAction: MessageRequestAction,
  validateDepartureMessageTypeActionProvider: ValidateMessageTypeActionProvider,
  msgGenService: MessageGenerationService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with ResponseHelper
    with ErrorTranslator
    with V2TestMessagesController {

  override def sendDepartureResponse(movementId: MovementId): Action[JsValue] =
    injectEISResponse(MovementType.Departure, movementId)

  override def sendArrivalsResponse(movementId: MovementId): Action[JsValue] =
    injectEISResponse(MovementType.Arrival, movementId)

  def injectEISResponse(movementType: MovementType, movementId: MovementId): Action[JsValue] =
    (authAction andThen messageRequestAction andThen validateDepartureMessageTypeActionProvider(movementType))
      .async(parse.json) {
        implicit request: MessageRequest[JsValue] =>
          implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

          (for {

            // Get matching departure from transit-movements
            _ <- (movementPersistenceService.getMovement(movementType, request.eori, movementId)).asPresentation

            message <- msgGenService.generateMessage(request.messageType, movementType, movementId).asPresentation

            // Send generated message to transit-movements-router
            messageId <- inboundRouterService
              .post(request.messageType, message, CorrelationId(movementId, MessageId(Constants.DefaultTriggerId)))
              .asPresentation

            // Check for matching departure message from transit-movements
            _ <- movementPersistenceService.getMessage(movementType, request.eori, movementId, messageId).asPresentation
          } yield HateoasResponse(movementType, movementId, request.messageType, message, messageId)).fold(
            presentationError => Status(presentationError.code.statusCode)(Json.toJson(presentationError)),
            Created(_)
          )
      }

}
