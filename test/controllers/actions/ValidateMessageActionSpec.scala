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

package controllers.actions

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.HeaderNames
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsNull
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import play.api.mvc.DefaultActionBuilder
import play.api.test.Helpers._
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ValidateMessageTypeActionSpec
    extends AnyFreeSpec
    with Matchers
    with GuiceOneAppPerSuite
    with OptionValues
    with ScalaFutures
    with MockitoSugar
    with BeforeAndAfterEach {
  override lazy val app = GuiceApplicationBuilder()
    .build()

  override def beforeEach(): Unit =
    super.beforeEach()

  class Harness(validateMessageTypeAction: ValidateMessageTypeAction, cc: ControllerComponents) extends BackendController(cc) {

    def post: Action[JsValue] = (DefaultActionBuilder.apply(cc.parsers.anyContent) andThen validateMessageTypeAction).async(cc.parsers.json) {
      _ =>
        Future.successful(Ok)
    }
  }

  "ValidateMessageTypeAction" - {
    "must execute the block when passed in a valid TestMessage" in {
      val validateMessageType = app.injector.instanceOf[ValidateMessageTypeAction]
      val cc                  = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE928"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must return BadRequest when passed in an invalid TestMessage" in {
      val validateMessageType = app.injector.instanceOf[ValidateMessageTypeAction]
      val cc                  = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "type": "IE928"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual BAD_REQUEST
    }

    "must return BadRequest when passed in an empty request" in {
      val validateMessageType = app.injector.instanceOf[ValidateMessageTypeAction]
      val cc                  = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(validateMessageType, cc)

      val req: FakeRequest[JsValue] = FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), JsNull)

      val result = controller.post()(req)

      status(result) mustEqual BAD_REQUEST
    }

    "must return NotImplemented when passed in an incorrect XML request" in {
      val validateMessageType = app.injector.instanceOf[ValidateMessageTypeAction]
      val cc                  = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE028"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual NOT_IMPLEMENTED
    }
  }
}
