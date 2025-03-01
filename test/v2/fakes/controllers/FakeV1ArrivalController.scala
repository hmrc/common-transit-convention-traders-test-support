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

package v2.fakes.controllers

import controllers.V1ArrivalTestMessagesController
import models.ArrivalId
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.BaseController
import play.api.mvc.Request
import play.api.test.Helpers.stubControllerComponents

class FakeV1ArrivalController extends BaseController with V1ArrivalTestMessagesController {

  override val controllerComponents = stubControllerComponents()

  override def injectEISResponse(arrivalId: ArrivalId): Action[JsValue] = Action(parse.json) {
    _ =>
      Accepted(Json.obj("version" -> 1))
  }

}
