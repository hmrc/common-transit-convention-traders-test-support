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

package routing

import org.apache.pekko.util.Timeout
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.HeaderNames
import play.api.http.Status.ACCEPTED
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.Json
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import play.api.test.Helpers.call
import play.api.test.Helpers.contentAsJson
import play.api.test.Helpers.status
import play.api.test.Helpers.stubControllerComponents
import v2_1.base.TestActorSystem
import v2_1.fakes.controllers.FakeTestMessagesController

import scala.concurrent.duration.DurationInt

class DeparturesRouterSpec extends AnyFreeSpec with Matchers with OptionValues with ScalaFutures with MockitoSugar with TestActorSystem {

  implicit private val timeout: Timeout = 5.seconds

  val sut = new DeparturesRouter(
    stubControllerComponents(),
    new FakeTestMessagesController()
  )

  "when requesting an EIS response" - {
    "with accept header set to application/vnd.hmrc.2.1+json" - {

      val departureHeaders = FakeHeaders(
        Seq(HeaderNames.ACCEPT -> "application/vnd.hmrc.2.1+json", HeaderNames.CONTENT_TYPE -> "application/json")
      )

      "must route to the v2 controller and return Accepted when successful" in {

        val request =
          FakeRequest(
            method = "POST",
            uri = routes.DeparturesRouter.injectEISResponse("1234567890abcdef", None).url,
            body = Json.obj("a" -> "1"),
            headers = departureHeaders
          )
        val result = call(sut.injectEISResponse("1234567890abcdef", None), request)

        status(result) mustBe ACCEPTED
        contentAsJson(result) mustBe Json.obj("version" -> 2.1) // ensure we get the unique value to verify we called the fake action
      }

      "must return bad request if an incorrect departure ID is provided" in {

        val request =
          FakeRequest(method = "POST", uri = routes.DeparturesRouter.injectEISResponse("a", None).url, body = Json.obj("a" -> "1"), headers = departureHeaders)
        val result = call(sut.injectEISResponse("1", None), request)

        status(result) mustBe BAD_REQUEST
      }

    }
  }
}
