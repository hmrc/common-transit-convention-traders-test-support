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

package v2.controllers

import base.SpecBase
import controllers.actions.AuthAction
import controllers.actions.FakeAuthAction
import data.TestXml
import generators.ModelGenerators
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.IntegrationPatience
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.HeaderNames
import play.api.http.Status.NOT_IMPLEMENTED
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import play.api.test.Helpers.POST
import play.api.test.Helpers.defaultAwaitTimeout
import play.api.test.Helpers.route
import play.api.test.Helpers.running
import play.api.test.Helpers.status
import routing.VersionedRouting

class V2DepartureTestMessagesControllerSpec
    extends SpecBase
    with ScalaCheckPropertyChecks
    with ModelGenerators
    with BeforeAndAfterEach
    with IntegrationPatience
    with TestXml {

  val exampleRequest = Json.parse(
    """{
        |     "message": {
        |         "messageType": "IE928"
        |     }
        | }""".stripMargin
  )

  "V2DepartureTestMessagesController" - {
    val v2Headers = FakeHeaders(Seq(HeaderNames.ACCEPT -> VersionedRouting.VERSION_2_ACCEPT_HEADER_VALUE))
    "POST" - {
      "must send a test message to the departures backend and return Created if successful" in {

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
          )
          .build()

        running(application) {
          val request =
            FakeRequest(
              method = POST,
              uri = routing.routes.DeparturesRouter.injectEISResponse("1").url,
              headers = v2Headers,
              body = exampleRequest
            )
          val result = route(application, request).value
          status(result) mustEqual (NOT_IMPLEMENTED)
        }
      }
    }
  }
}
