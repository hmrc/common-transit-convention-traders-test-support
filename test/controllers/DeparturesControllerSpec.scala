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

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import base.SpecBase
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.BeforeAndAfterEach
import org.mockito.Mockito._
import org.scalacheck.Arbitrary.arbitrary
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
import cats.data.NonEmptyList
import connectors.DepartureConnector
import models.MessageStatus.SubmissionPending
import models.Departure
import models.DepartureId
import models.DepartureStatus
import models.MessageType
import models.MessageWithStatus
import models.MessageWithoutStatus
import org.scalacheck.Arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.AnyContentAsXml
import repositories.DepartureRepository
import uk.gov.hmrc.http.HttpResponse
import utils.Format

import scala.concurrent.Future

class DeparturesControllerSpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators with BeforeAndAfterEach with IntegrationPatience {

  val localDate     = LocalDate.now()
  val localTime     = LocalTime.of(1, 1)
  val localDateTime = LocalDateTime.of(localDate, localTime)

  val newDepartureId = arbitrary[DepartureId].sample.value

  val requestXmlBody =
    <CC015B>
      <DatOfPreMES9>{Format.dateFormatted(localDate)}</DatOfPreMES9>
      <TimOfPreMES10>{Format.timeFormatted(localTime)}</TimOfPreMES10>
      <HEAHEA>
        <RefNumHEA4>abc</RefNumHEA4>
      </HEAHEA>
    </CC015B>

  val message = MessageWithStatus(
    localDateTime,
    MessageType.DepartureDeclaration,
    requestXmlBody,
    SubmissionPending,
    1
  )

  val messageWithoutStatus = MessageWithoutStatus(
    localDateTime,
    MessageType.DepartureDeclaration,
    requestXmlBody,
    1
  )

  val initializedDeparture = Departure(
    departureId = newDepartureId,
    eoriNumber = "eori",
    movementReferenceNumber = None,
    referenceNumber = "referenceNumber",
    status = DepartureStatus.Initialized,
    created = localDateTime,
    updated = localDateTime,
    nextMessageCorrelationId = message.messageCorrelationId + 1,
    messages = NonEmptyList.one(message)
  )

  val exampleRequest = Json.parse(
    """{
      |     "message": {
      |         "messageType": "IE928"
      |     }
      | }""".stripMargin
  )

  "POST" - {
    "must send a test message to the departures backend and return Created if successful" in {
      val mockDepartureRepository = mock[DepartureRepository]
      val mockDepartureConnector  = mock[DepartureConnector]

      val departure = Arbitrary.arbitrary[Departure].sample.value.copy(eoriNumber = "eori")
      when(mockDepartureRepository.get(any())).thenReturn(Future.successful(Some(departure)))
      when(mockDepartureConnector.post(any(), any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[DepartureRepository].toInstance(mockDepartureRepository),
          bind[DepartureConnector].toInstance(mockDepartureConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departure.departureId).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual CREATED
      }
    }

    "must return UnsupportedMediaType when no Content-Type specified" in {
      val departure = Arbitrary.arbitrary[Departure].sample.value.copy(eoriNumber = "eori")

      val application = baseApplicationBuilder
        .build()

      running(application) {
        val request = FakeRequest(method = POST,
                                  uri = routes.DepartureTestMessagesController.injectEISResponse(departure.departureId).url,
                                  headers = FakeHeaders(Nil),
                                  body = AnyContentAsEmpty)

        val result = route(application, request).value

        status(result) mustEqual UNSUPPORTED_MEDIA_TYPE
      }
    }

    "must return UnsupportedMediaType when invalid Content-Type specified" in {
      val departure = Arbitrary.arbitrary[Departure].sample.value.copy(eoriNumber = "eori")

      val application = baseApplicationBuilder
        .build()

      running(application) {
        val request = FakeRequest(
          method = POST,
          uri = routes.DepartureTestMessagesController.injectEISResponse(departure.departureId).url,
          headers = FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/xml")),
          body = AnyContentAsXml(<xml></xml>)
        )

        val result = route(application, request).value

        status(result) mustEqual UNSUPPORTED_MEDIA_TYPE
      }
    }

    "must return NotImplemented if specified message type is not supported" in {
      val departure = Arbitrary.arbitrary[Departure].sample.value.copy(eoriNumber = "eori")

      val invalidRequest = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE028"
          |     }
          | }""".stripMargin
      )

      val application = baseApplicationBuilder
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departure.departureId).url).withJsonBody(invalidRequest)

        val result = route(application, request).value

        status(result) mustEqual NOT_IMPLEMENTED
      }
    }

    "must return InternalServerError if database error occurs when retrieving departure" in {
      val mockDepartureRepository = mock[DepartureRepository]

      val departure = Arbitrary.arbitrary[Departure].sample.value.copy(eoriNumber = "eori")
      when(mockDepartureRepository.get(any())).thenReturn(Future.failed(new Exception))

      val application = baseApplicationBuilder
        .overrides(
          bind[DepartureRepository].toInstance(mockDepartureRepository)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departure.departureId).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "must return NotFound if no departure found for specified id" in {
      val mockDepartureRepository = mock[DepartureRepository]

      val departure = Arbitrary.arbitrary[Departure].sample.value.copy(eoriNumber = "eori")
      when(mockDepartureRepository.get(any())).thenReturn(Future.successful(None))

      val application = baseApplicationBuilder
        .overrides(
          bind[DepartureRepository].toInstance(mockDepartureRepository)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departure.departureId).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual NOT_FOUND
      }
    }

    "must return NotFound when there are no messages with status for specified departure" in {
      val mockDepartureRepository = mock[DepartureRepository]
      val mockDepartureConnector  = mock[DepartureConnector]

      val departure = Arbitrary.arbitrary[Departure].sample.value.copy(eoriNumber = "eori").copy(messages = NonEmptyList.one(messageWithoutStatus))
      when(mockDepartureRepository.get(any())).thenReturn(Future.successful(Some(departure)))
      when(mockDepartureConnector.post(any(), any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[DepartureRepository].toInstance(mockDepartureRepository),
          bind[DepartureConnector].toInstance(mockDepartureConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departure.departureId).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual NOT_FOUND
      }
    }

    "must return BadRequest if departures backend returns BadRequest" in {
      val mockDepartureRepository = mock[DepartureRepository]
      val mockDepartureConnector  = mock[DepartureConnector]

      val departure = Arbitrary.arbitrary[Departure].sample.value.copy(eoriNumber = "eori")
      when(mockDepartureRepository.get(any())).thenReturn(Future.successful(Some(departure)))
      when(mockDepartureConnector.post(any(), any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[DepartureRepository].toInstance(mockDepartureRepository),
          bind[DepartureConnector].toInstance(mockDepartureConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departure.departureId).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    "must return InternalServerError if departures backend returns InternalServerError" in {
      val mockDepartureRepository = mock[DepartureRepository]
      val mockDepartureConnector  = mock[DepartureConnector]

      val departure = Arbitrary.arbitrary[Departure].sample.value.copy(eoriNumber = "eori")
      when(mockDepartureRepository.get(any())).thenReturn(Future.successful(Some(departure)))
      when(mockDepartureConnector.post(any(), any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[DepartureRepository].toInstance(mockDepartureRepository),
          bind[DepartureConnector].toInstance(mockDepartureConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departure.departureId).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }
  }
}
