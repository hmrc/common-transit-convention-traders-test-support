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

package v2_1.controllers

import com.google.inject.ImplementedBy
import config.Constants
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import v2_1.controllers.actions.AuthAction
import v2_1.controllers.actions.MessageRequestAction
import v2_1.controllers.actions.ValidateMessageTypeActionProvider
import v2_1.models.*
import v2_1.models.request.MessageRequest
import v2_1.services.InboundRouterService
import v2_1.services.MessageGenerationService
import v2_1.services.MovementPersistenceService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

@ImplementedBy(classOf[TestMessagesControllerImpl])
trait TestMessagesController {
  def sendDepartureResponse(departureId: MovementId, messageId: Option[String]): Action[JsValue]

  def sendArrivalsResponse(departureId: MovementId, messageId: Option[String]): Action[JsValue]
}

class TestMessagesControllerImpl @Inject() (
  cc: ControllerComponents,
  authAction: AuthAction,
  movementPersistenceService: MovementPersistenceService,
  inboundRouterService: InboundRouterService,
  messageRequestAction: MessageRequestAction,
  validateDepartureMessageTypeActionProvider: ValidateMessageTypeActionProvider,
  msgGenService: MessageGenerationService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with ErrorTranslator
    with TestMessagesController {

  override def sendDepartureResponse(movementId: MovementId, messageId: Option[String]): Action[JsValue] =
    injectEISResponse(MovementType.Departure, movementId, messageId)

  override def sendArrivalsResponse(movementId: MovementId, messageId: Option[String]): Action[JsValue] =
    injectEISResponse(MovementType.Arrival, movementId, messageId)

  def injectEISResponse(movementType: MovementType, movementId: MovementId, messageId: Option[String]): Action[JsValue] =
    (authAction andThen messageRequestAction andThen validateDepartureMessageTypeActionProvider(movementType))
      .async(parse.json) {
        implicit request: MessageRequest[JsValue] =>
          implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

          (for {
            _       <- movementPersistenceService.getMovement(movementType, request.eori, movementId).asPresentation
            message <- msgGenService.generateMessage(request.messageType, movementType, movementId).asPresentation
            messageId <- inboundRouterService
              .post(request.messageType, message, CorrelationId(movementId, MessageId(messageId.getOrElse(Constants.DefaultTriggerId))))
              .asPresentation
            _ <- movementPersistenceService.getMessage(movementType, request.eori, movementId, messageId).asPresentation
          } yield HateoasResponse(movementType, movementId, request.messageType, message, messageId)).fold(
            presentationError => Status(presentationError.code.statusCode)(Json.toJson(presentationError)),
            Created(_)
          )
      }

}
