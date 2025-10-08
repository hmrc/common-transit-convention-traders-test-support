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

import config.Constants
import controllers.actions.AuthAction
import controllers.actions.MessageRequestRefiner
import controllers.actions.ValidateAcceptRefiner
import models.*
import models.MovementType.Arrival
import models.MovementType.Departure
import models.errors.PresentationError
import models.request.MessageRequest
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import services.InboundRouterService
import services.MessageGenerationService
import services.MovementPersistenceService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class TestMessagesController @Inject() (
  cc: ControllerComponents,
  authAction: AuthAction,
  movementPersistenceService: MovementPersistenceService,
  inboundRouterService: InboundRouterService,
  msgGenService: MessageGenerationService,
  validateAcceptRefiner: ValidateAcceptRefiner,
  messageRequestAction: MessageRequestRefiner
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with ErrorTranslator {

  def arrival(movementId: MovementId, messageId: Option[MessageId]): Action[JsValue] =
    injectEISResponse(Arrival, movementId, messageId)

  def departure(movementId: MovementId, messageId: Option[MessageId]): Action[JsValue] =
    injectEISResponse(Departure, movementId, messageId)

  def injectEISResponse(movementType: MovementType, movementId: MovementId, messageId: Option[MessageId]): Action[JsValue] =
    (authAction andThen validateAcceptRefiner andThen messageRequestAction(movementType)).async(parse.json) {
      implicit request: MessageRequest[JsValue] =>
        implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)
        val eori                       = request.eori
        val messageType                = request.messageType

        (for {
          _         <- movementPersistenceService.getMovement(movementType, eori, movementId).asPresentation
          message   <- msgGenService.generateMessage(messageType, movementType, movementId).asPresentation
          messageId <- inboundRouterService
            .post(messageType, message, CorrelationId(movementId, messageId.getOrElse(MessageId(Constants.DefaultTriggerId))))
            .asPresentation
          _ <- movementPersistenceService.getMessage(movementType, eori, movementId, messageId).asPresentation
        } yield HateoasResponse(movementType, movementId, messageType, message, messageId)).fold(
          presentationError => Status(presentationError.code.statusCode)(Json.toJson(presentationError)),
          Created(_)
        )
    }

}
