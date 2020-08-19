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

import models.MessageType
import models.TestMessage
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.HeaderNames
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import play.api.mvc.DefaultActionBuilder
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import utils.Messages

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.xml.Elem
import scala.xml.NodeSeq
import scala.xml.XML

class ValidateArrivalMessageTypeActionSpec
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

  class Harness(validateArrivalMessageTypeAction: ValidateArrivalMessageTypeAction, cc: ControllerComponents) extends BackendController(cc) {

    def post: Action[JsValue] = (DefaultActionBuilder.apply(cc.parsers.anyContent) andThen validateArrivalMessageTypeAction).async(cc.parsers.json) {
      request: GeneratedMessageRequest[JsValue] =>
        Future.successful(Ok(request.generatedMessage))
    }
  }

  private def contentAsXml(of: Future[Result]): Elem = XML.loadString(contentAsString(of))

  private def numberOfNodes(nodes: NodeSeq): Int =
    nodes.head.child.filterNot(_.toString().trim.isEmpty).length

  "ValidateArrivalMessageTypeAction" - {
    "must execute the block when passed in a valid IE008 TestMessage" in {
      val validateMessageType = app.injector.instanceOf[ValidateArrivalMessageTypeAction]
      val cc                  = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE008"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must execute the block when passed in a valid IE025 TestMessage" in {
      val validateMessageType = app.injector.instanceOf[ValidateArrivalMessageTypeAction]
      val cc                  = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE025"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must execute the block when passed in a valid IE043 TestMessage" in {
      val validateMessageType = app.injector.instanceOf[ValidateArrivalMessageTypeAction]
      val cc                  = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE043"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must execute the block when passed in a valid IE058 TestMessage" in {
      val validateMessageType = app.injector.instanceOf[ValidateArrivalMessageTypeAction]
      val cc                  = app.injector.instanceOf[ControllerComponents]

      val controller = new Harness(validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE058"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
    }

    "must generate correct IE008 message in executed block" in {
      val validateMessageType = app.injector.instanceOf[ValidateArrivalMessageTypeAction]
      val cc                  = app.injector.instanceOf[ControllerComponents]

      val ie008: NodeSeq = Messages.Arrival.SupportedMessageTypes(TestMessage(MessageType.ArrivalRejection.code))()

      val controller = new Harness(validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE008"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
      numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie008)
    }

    "must generate correct IE025 message in executed block" in {
      val validateMessageType = app.injector.instanceOf[ValidateArrivalMessageTypeAction]
      val cc                  = app.injector.instanceOf[ControllerComponents]

      val ie025: NodeSeq = Messages.Arrival.SupportedMessageTypes(TestMessage(MessageType.GoodsReleased.code))()

      val controller = new Harness(validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE025"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
      numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie025)
    }

    "must generate correct IE043 message in executed block" in {
      val validateMessageType = app.injector.instanceOf[ValidateArrivalMessageTypeAction]
      val cc                  = app.injector.instanceOf[ControllerComponents]

      val ie043: NodeSeq = Messages.Arrival.SupportedMessageTypes(TestMessage(MessageType.UnloadingPermission.code))()

      val controller = new Harness(validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE043"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
      numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie043)
    }

    "must generate correct IE058 message in executed block" in {
      val validateMessageType = app.injector.instanceOf[ValidateArrivalMessageTypeAction]
      val cc                  = app.injector.instanceOf[ControllerComponents]

      val ie058: NodeSeq = Messages.Arrival.SupportedMessageTypes(TestMessage(MessageType.UnloadingRemarksRejection.code))()

      val controller = new Harness(validateMessageType, cc)

      val exampleRequest: JsValue = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE058"
          |     }
          | }""".stripMargin
      )

      val req: FakeRequest[JsValue] =
        FakeRequest(method = "", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), exampleRequest)

      val result = controller.post()(req)

      status(result) mustEqual OK
      numberOfNodes(contentAsXml(result)) mustEqual numberOfNodes(ie058)
    }
  }
}
