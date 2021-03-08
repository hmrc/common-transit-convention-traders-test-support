/*
 * Copyright 2021 HM Revenue & Customs
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

import models.MessageType
import models.generation.TestMessage
import models.request.MessageRequest
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
import play.api.mvc.DefaultActionBuilder
import play.api.mvc.Result
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import utils.Messages

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.xml.Elem
import scala.xml.NodeSeq
import scala.xml.XML

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

  class Harness(messageRequestAction: MessageRequestAction, validateMessageTypeAction: ValidateDepartureMessageTypeAction, cc: ControllerComponents)
      extends BackendController(cc) {

    def post: Action[JsValue] =
      (DefaultActionBuilder.apply(cc.parsers.anyContent) andThen messageRequestAction andThen validateMessageTypeAction).async(cc.parsers.json) {
        _ =>
          Future.successful(Ok(JsString("test")))
      }
  }

  private def contentAsXml(of: Future[Result]): Elem = XML.loadString(contentAsString(of))

  private def numberOfNodes(nodes: NodeSeq): Int =
    nodes.head.child.filterNot(_.toString().trim.isEmpty).length

  "ValidateDepartureMessageTypeAction" - {
    "must execute the block when passed in a valid IE928 TestMessage" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

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
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

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
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

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
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

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
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

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
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

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
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

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
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

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
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

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
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

      //TODO: Replace by having generator test specs
      //val ie928: NodeSeq = Messages.Departure.SupportedMessageTypes(TestMessage(MessageType.PositiveAcknowledgement.code))()

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
      //TODO: Replace by having generator test specs
      //numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie928)
    }

    "must generate correct IE051 message in executed block" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

      //TODO: Replace by having generator test specs
      //val ie051: NodeSeq = Messages.Departure.SupportedMessageTypes(TestMessage(MessageType.NoReleaseForTransit.code))()

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
      //TODO: Replace by having generator test specs
      //numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie051)
    }

    "must generate correct IE029 message in executed block" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

      //TODO: Replace by having generator test specs
      //val ie029: NodeSeq = Messages.Departure.SupportedMessageTypes(TestMessage(MessageType.ReleaseForTransit.code))()

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
      //TODO: Replace by having generator test specs
      //numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie029)
    }

    "must generate correct IE060 message in executed block" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

      //TODO: Replace by having generator test specs
      //val ie060: NodeSeq = Messages.Departure.SupportedMessageTypes(TestMessage(MessageType.ControlDecisionNotification.code))()

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
      //TODO: Replace by having generator test specs
      //numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie060)
    }

    "must generate correct IE028 message in executed block" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

      //TODO: Replace by having generator test specs
      //val ie028: NodeSeq = Messages.Departure.SupportedMessageTypes(TestMessage(MessageType.MrnAllocated.code))()

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
      //TODO: Replace by having generator test specs
      //numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie028)
    }

    "must generate correct IE016 message in executed block" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

      //TODO: Replace by having generator test specs
      //val ie016: NodeSeq = Messages.Departure.SupportedMessageTypes(TestMessage(MessageType.DeclarationRejected.code))()

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
      //TODO: Replace by having generator test specs
      //numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie016)
    }

    "must generate correct IE009 message in executed block" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

      //TODO: Replace by having generator test specs
      //val ie009: NodeSeq = Messages.Departure.SupportedMessageTypes(TestMessage(MessageType.CancellationDecision.code))()

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
      //TODO: Replace by having generator test specs
      //numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie009)
    }

    "must generate correct IE045 message in executed block" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

      //TODO: Replace by having generator test specs
      //val ie045: NodeSeq = Messages.Departure.SupportedMessageTypes(TestMessage(MessageType.WriteOffNotification.code))()

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
      //TODO: Replace by having generator test specs
      //      numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie045)
    }

    "must generate correct IE055 message in executed block" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

      //TODO: Replace by having generator test specs
      //val ie055: NodeSeq = Messages.Departure.SupportedMessageTypes(TestMessage(MessageType.GuaranteeNotValid.code))()

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

      //TODO: Replace by having generator test specs
//      numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie055)
    }

    "must return BadRequest when passed in an invalid TestMessage" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

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

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

      val req: FakeRequest[JsValue] = FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), JsNull)

      val result = controller.post()(req)

      status(result) mustEqual BAD_REQUEST
    }

    "must return NotImplemented when passed in TestMessage has unsupported message type" in {
      val validateMessageType  = app.injector.instanceOf[ValidateDepartureMessageTypeAction]
      val messageRequestAction = app.injector.instanceOf[MessageRequestAction]
      val cc                   = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(messageRequestAction, validateMessageType, cc)

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
