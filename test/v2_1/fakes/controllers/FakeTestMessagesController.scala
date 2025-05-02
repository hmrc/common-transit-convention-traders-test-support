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

package v2_1.fakes.controllers

import org.apache.pekko.stream.Materializer
import com.google.inject.Inject
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import v2_1.controllers.TestMessagesController
import v2_1.controllers.stream.StreamingParsers
import v2_1.models.MovementId

class FakeTestMessagesController @Inject() ()(implicit val materializer: Materializer)
    extends BaseController
    with TestMessagesController
    with StreamingParsers {

  override val controllerComponents: ControllerComponents = stubControllerComponents()

  override def sendDepartureResponse(departureId: MovementId, messageId: Option[String]): Action[JsValue] = Action(parse.json) {
    _ =>
      Accepted(Json.obj("version" -> 2.1))
  }

  override def sendArrivalsResponse(departureId: MovementId, messageId: Option[String]): Action[JsValue] = Action(parse.json) {
    _ =>
      Accepted(Json.obj("version" -> 2.1))
  }
}
