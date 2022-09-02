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

package v2.fakes.controllers

import controllers.V1DepartureTestMessagesController
import models.DepartureId
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.BaseController
import play.api.mvc.Request
import play.api.test.Helpers.stubControllerComponents

import scala.xml.NodeSeq

class FakeV1DeparturesController extends BaseController with V1DepartureTestMessagesController {

  override val controllerComponents = stubControllerComponents()

  override def injectEISResponse(departureId: DepartureId): Action[JsValue] = Action(parse.json) {
    _: Request[JsValue] =>
      Accepted(Json.obj("version" -> 1))
  }

  override def submitDepartureDeclaration: Action[NodeSeq] = Action(parse.xml) {
    _: Request[NodeSeq] =>
      Accepted(Json.obj("version" -> 1))
  }

}
