/*
 * Copyright 2025 HM Revenue & Customs
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

///*
// * Copyright 2023 HM Revenue & Customs
// *
// * Licensed under the Apache License, VersionHeader 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package controllers.actions
//
//import controllers.actions.AuthRequest
//import controllers.actions.MessageRequestRefiner
//import models.EORINumber
//import models.MessageType
//import models.request.MessageRequest
//import org.scalacheck.Arbitrary.arbitrary
//import org.scalacheck.Gen
//import org.scalatest.concurrent.ScalaFutures
//import org.scalatest.freespec.AnyFreeSpec
//import org.scalatest.matchers.must.Matchers
//import org.scalatestplus.mockito.MockitoSugar
//import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
//import play.api.http.HeaderNames
//import play.api.http.Status.BAD_REQUEST
//import play.api.libs.json.JsObject
//import play.api.libs.json.JsString
//import play.api.libs.json.JsValue
//import play.api.mvc.Result
//import play.api.test.FakeHeaders
//import play.api.test.FakeRequest
//import versioned.v2_1.generators.ModelGenerators
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//

//TODO fix these tests
//class MessageRequestActionSpec extends AnyFreeSpec with ScalaFutures with Matchers with MockitoSugar with ScalaCheckDrivenPropertyChecks with ModelGenerators {
//
//  object Harness extends MessageRequestRefiner() {
//
//    def execute[A](request: AuthRequest[A]): Future[Either[Result, MessageRequest[A]]] =
//      refine(request)
//  }
//
//  MessageType.values.foreach {
//    messageType =>
//      s"must produce ${messageType.code} with default values if only ${messageType.code} specified" in forAll(arbitrary[EORINumber]) {
//        eoriNumber =>
//          val body = JsObject(Seq("message" -> JsObject(Seq("messageType" -> JsString(messageType.code)))))
//
//          val request: FakeRequest[JsValue] =
//            FakeRequest(method = "POST", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), body)
//
//          val authAction = AuthRequest(request, eoriNumber.value)
//
//          whenReady(Harness.execute(authAction)) {
//            case Right(result: MessageRequest[_]) =>
//              result.eori mustBe eoriNumber
//              result.messageType mustBe messageType
//            case _ => fail("A valid message type was not extracted")
//          }
//      }
//  }
//
//  "must produce an error if an invalid message type was found" in forAll(Gen.alphaNumStr, arbitrary[EORINumber]) {
//    (messageType, eoriNumber) =>
//      val body = JsObject(Seq("message" -> JsObject(Seq("messageType" -> JsString(messageType)))))
//
//      val request: FakeRequest[JsValue] =
//        FakeRequest(method = "POST", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), body)
//
//      val authAction = AuthRequest(request, eoriNumber.value)
//
//      whenReady(Harness.execute(authAction)) {
//        case Left(result) => result.header.status mustBe BAD_REQUEST
//        case Right(x)     => fail(s"A valid type (${x.messageType.code}) was selected when it shouldn't have been")
//      }
//  }
//}
