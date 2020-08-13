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

package controllers

import base.SpecBase
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.BeforeAndAfterEach
import org.mockito.Mockito._
import generators.ModelGenerators
import org.mockito.ArgumentMatchers.any
import play.api.inject.bind
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import play.api.test.Helpers.POST
import play.api.test.Helpers.route
import play.api.test.Helpers.running
import play.api.test.Helpers.status
import play.api.test.Helpers._
import connectors.DepartureConnector
import controllers.actions.AuthAction
import controllers.actions.FakeAuthAction
import models.DepartureId
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.AnyContentAsXml
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class DepartureTestMessagesControllerSpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators with BeforeAndAfterEach with IntegrationPatience {

  val departureId = new DepartureId(1)

  val exampleRequest = Json.parse(
    """{
      |     "message": {
      |         "messageType": "IE928"
      |     }
      | }""".stripMargin
  )

  "POST" - {
    "must send a test message to the departures backend and return Created if successful" in {
      val mockDepartureConnector = mock[DepartureConnector]

      when(mockDepartureConnector.post(any(), any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[DepartureConnector].toInstance(mockDepartureConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual CREATED
      }
    }

    "must return UnsupportedMediaType when no Content-Type specified" in {
      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction]
        )
        .build()

      running(application) {
        val request = FakeRequest(method = POST,
                                  uri = routes.DepartureTestMessagesController.injectEISResponse(departureId).url,
                                  headers = FakeHeaders(Nil),
                                  body = AnyContentAsEmpty)

        val result = route(application, request).value

        status(result) mustEqual UNSUPPORTED_MEDIA_TYPE
      }
    }

    "must return UnsupportedMediaType when invalid Content-Type specified" in {
      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction]
        )
        .build()

      running(application) {
        val request = FakeRequest(
          method = POST,
          uri = routes.DepartureTestMessagesController.injectEISResponse(departureId).url,
          headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/xml")),
          body = AnyContentAsXml(<xml></xml>)
        )

        val result = route(application, request).value

        status(result) mustEqual UNSUPPORTED_MEDIA_TYPE
      }
    }

    "must return NotImplemented if specified message type is not supported" in {
      val invalidRequest = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE005"
          |     }
          | }""".stripMargin
      )

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction]
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(invalidRequest)

        val result = route(application, request).value

        status(result) mustEqual NOT_IMPLEMENTED
      }
    }

    "must return BadRequest if departures backend returns BadRequest" in {
      val mockDepartureConnector = mock[DepartureConnector]

      when(mockDepartureConnector.post(any(), any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[DepartureConnector].toInstance(mockDepartureConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    "must return InternalServerError if departures backend returns InternalServerError" in {
      val mockDepartureConnector = mock[DepartureConnector]

      when(mockDepartureConnector.post(any(), any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[DepartureConnector].toInstance(mockDepartureConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }
  }
}
