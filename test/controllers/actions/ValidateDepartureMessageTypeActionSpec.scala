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
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import play.api.mvc.DefaultActionBuilder
import play.api.test.Helpers._
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global
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

  class Harness(channelAction: ChannelAction,
                messageRequestAction: MessageRequestAction,
                validateMessageTypeAction: ValidateDepartureMessageTypeAction,
                cc: ControllerComponents)
      extends BackendController(cc) {

    def post: Action[JsValue] =
      (DefaultActionBuilder.apply(cc.parsers.anyContent) andThen channelAction andThen messageRequestAction andThen validateMessageTypeAction)
        .async(cc.parsers.json) {
          _ =>
            Future.successful(Ok(JsString("test")))
        }
  }

  "ValidateDepartureMessageTypeAction" - {
    "must execute the block when passed in a valid IE928 TestMessage" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

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

    "must execute the block when passed in a valid IE051 TestMessage" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE051"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must execute the block when passed in a valid IE029 TestMessage" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE029"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must execute the block when passed in a valid IE060 TestMessage" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE060"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must execute the block when passed in a valid IE028 TestMessage" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

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

      status(result) mustEqual OK
    }

    "must execute the block when passed in a valid IE016 TestMessage" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE016"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must execute the block when passed in a valid IE009 TestMessage" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE009"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must execute the block when passed in a valid IE045 TestMessage" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE045"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must execute the block when passed in a valid IE055 TestMessage" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE055"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must generate correct IE928 message in executed block" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

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

    "must generate correct IE051 message in executed block" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE051"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must generate correct IE029 message in executed block" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE029"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must generate correct IE060 message in executed block" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE060"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must generate correct IE028 message in executed block" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

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

      status(result) mustEqual OK
    }

    "must generate correct IE016 message in executed block" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE016"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must generate correct IE009 message in executed block" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE009"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must generate correct IE045 message in executed block" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE045"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must generate correct IE055 message in executed block" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE055"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must return BadRequest when passed in an invalid TestMessage" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

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
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

      val req: FakeRequest[JsValue] = FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), JsNull)

      val result = controller.post()(req)

      status(result) mustEqual BAD_REQUEST
    }

    "must return NotImplemented when passed in TestMessage has unsupported message type" in {
      val channelAction        = app.injector.instanceOf[FakeChannelAction]
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(channelAction, messageRequestAction, validateMessageType, cc)

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
