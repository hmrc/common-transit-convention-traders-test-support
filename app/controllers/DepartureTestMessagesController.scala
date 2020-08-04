/*
 * Copyright 2020 HM Revenue & Customs
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
import connectors.DepartureConnector
import controllers.actions.AuthAction
import controllers.actions.ValidateMessageTypeAction
import javax.inject.Inject
import models.DepartureId
import models.TestMessage
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import utils.Messages
import utils.ResponseHelper

import scala.concurrent.ExecutionContext

class DepartureTestMessagesController @Inject()(cc: ControllerComponents,
                                                departureConnector: DepartureConnector,
                                                authAction: AuthAction,
                                                validateMessageTypeAction: ValidateMessageTypeAction)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with HttpErrorFunctions
    with ResponseHelper {

  def injectEISResponse(departureId: DepartureId): Action[JsValue] =
    (authAction andThen validateMessageTypeAction).async(parse.json) {
      implicit request: Request[JsValue] =>
        val testMessage: TestMessage = request.body.as[TestMessage]

        val ieMessage = Messages.SupportedMessageTypes(testMessage.message.messageType)().toString()

        departureConnector.post(testMessage.message.messageType, ieMessage, departureId, Constants.MessageCorrelationId).map {
          response =>
            response.status match {
              case status if is2xx(status) =>
                Created
              case _ =>
                handleNon2xx(response)
            }
        }
    }
}
