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

package controllers.actions

import models.ChannelType
import models.generation.UnloadingPermissionGenInstructions
import models.request.ChannelRequest
import models.request.MessageRequest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.HeaderNames
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.mvc.Result
import play.api.test.FakeHeaders
import play.api.test.FakeRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MessageRequestActionSpec extends AnyFreeSpec with ScalaFutures with Matchers with MockitoSugar {

  class Harness extends MessageRequestAction() {

    def execute[A](request: ChannelRequest[A]): Future[Either[Result, MessageRequest[A]]] =
      refine(request)
  }

  "must produce IE043 with default values if only IE043 specificed" in {
    val harness = new Harness

    val body = JsObject(Seq("message" -> JsObject(Seq("messageType" -> JsString("IE043")))))

    val request: FakeRequest[JsValue] =
      FakeRequest(method = "POST", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), body)

    val channelRequest = new ChannelRequest(request, ChannelType.api)
    val result         = harness.execute(channelRequest).futureValue.right.get.instructions.asInstanceOf[UnloadingPermissionGenInstructions]

    result.sealsCount mustBe 1
    result.goodsCount mustBe 1
    result.specialMentionsCount mustBe 1
    result.productCount mustBe 1
  }

  "must produce IE043 with assigned values if provided" in {
    val harness = new Harness

    val body = JsObject(
      Seq(
        "message" -> JsObject(
          Seq("messageType"          -> JsString("IE043"),
              "goodsCount"           -> JsNumber(4),
              "productCount"         -> JsNumber(5),
              "specialMentionsCount" -> JsNumber(6),
              "sealsCount"           -> JsNumber(7)))))

    val request: FakeRequest[JsValue] =
      FakeRequest(method = "POST", uri = "", headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")), body)

    val channelRequest = new ChannelRequest(request, ChannelType.api)
    val result         = harness.execute(channelRequest).futureValue.right.get.instructions.asInstanceOf[UnloadingPermissionGenInstructions]

    result.goodsCount mustBe 4
    result.productCount mustBe 5
    result.specialMentionsCount mustBe 6
    result.sealsCount mustBe 7
  }
}
