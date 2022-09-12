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

package v2.controllers.actions

import controllers.actions.AuthAction
import controllers.actions.FakeAuthAction
import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.HeaderNames
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsNull
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.Future

class ValidateDepartureMessageTypeActionSpec
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

  class Harness(authActionBuilder: AuthAction,
                messageRequestAction: MessageRequestAction,
                validateMessageTypeAction: ValidateDepartureMessageTypeAction,
                cc: ControllerComponents)
      extends BackendController(cc) {

    def post: Action[JsValue] =
      (/*DefaultActionBuilder.apply(cc.parsers.anyContent)*/ authActionBuilder andThen messageRequestAction andThen validateMessageTypeAction)
        .async(cc.parsers.json) {
          _ =>
            Future.successful(Ok(JsString("test")))
        }
  }

  "ValidateDepartureMessageTypeAction" - {
    "must execute the block when passed in a valid IE928 TestMessage" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]
      val authAction           = app.injector.instanceOf[FakeAuthAction]

      val controller = new Harness(authAction, messageRequestAction, validateMessageType, cc)

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

    "must generate correct IE928 message in executed block" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]
      val authAction           = app.injector.instanceOf[FakeAuthAction]

      val controller = new Harness(authAction, messageRequestAction, validateMessageType, cc)

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
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]
      val authAction           = app.injector.instanceOf[FakeAuthAction]

      val controller = new Harness(authAction, messageRequestAction, validateMessageType, cc)

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
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]
      val authAction           = app.injector.instanceOf[FakeAuthAction]

      val controller = new Harness(authAction, messageRequestAction, validateMessageType, cc)

      val req: FakeRequest[JsValue] = FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), JsNull)

      val result = controller.post()(req)

      status(result) mustEqual BAD_REQUEST
    }

    "must return NotImplemented when passed in TestMessage has unsupported message type" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]
      val authAction           = app.injector.instanceOf[FakeAuthAction]

      val controller = new Harness(authAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE005"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual BAD_REQUEST
    }
  }
}
