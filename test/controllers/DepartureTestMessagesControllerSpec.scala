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

package controllers

import base.SpecBase
import connectors.DepartureConnector
import connectors.DepartureMessageConnector
import connectors.InboundRouterConnector
import controllers.actions.AuthAction
import controllers.actions.ChannelAction
import controllers.actions.FakeAuthAction
import controllers.actions.FakeChannelAction
import controllers.actions.FakeVersionOneEnabledCheckAction
import controllers.actions.VersionOneEnabledCheckAction
import data.TestXml
import generators.ModelGenerators
import models.MessageType.DepartureDeclaration
import models.MessageType.PositiveAcknowledgement
import models.domain.MovementMessage
import models.ChannelType
import models.Departure
import models.DepartureId
import models.DepartureWithMessages
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.IntegrationPatience
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.HeaderNames
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.AnyContentAsXml
import play.api.test.Helpers.POST
import play.api.test.Helpers.route
import play.api.test.Helpers.running
import play.api.test.Helpers.status
import play.api.test.Helpers._
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HttpResponse
import utils.Format

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import scala.concurrent.Future
import scala.xml.Utility.trim
import scala.xml.Elem
import scala.xml.XML

class DepartureTestMessagesControllerSpec
    extends SpecBase
    with ScalaCheckPropertyChecks
    with ModelGenerators
    with BeforeAndAfterEach
    with IntegrationPatience
    with TestXml {

  val departureId = new DepartureId(1)

  val departure = Departure(1, "loc", "messageLoc", Some("mrn"), "ref", "status", LocalDateTime.now(), LocalDateTime.now())

  val departureWithMessages = DepartureWithMessages(
    1,
    "loc",
    "messageLoc",
    Some("mrn"),
    "ref",
    "status",
    LocalDateTime.now(),
    LocalDateTime.now(),
    Seq(MovementMessage("/1", LocalDateTime.now(), "type", CC015B))
  )

  val movement = MovementMessage("/transits-movements-trader-at-departure/movements/departures/1/messages/2", LocalDateTime.now, "abc", <test>default</test>)

  val exampleRequest = Json.parse(
    """{
      |     "message": {
      |         "messageType": "IE928"
      |     }
      | }""".stripMargin
  )
  private val localDate = LocalDate.now()
  private val localTime = LocalTime.of(1, 1)

  private val requestXmlBody =
    <CC015B>
      <SynVerNumMES2>123</SynVerNumMES2>
      <DatOfPreMES9>{Format.dateFormatted(localDate)}</DatOfPreMES9>
      <TimOfPreMES10>{Format.timeFormatted(localTime)}</TimOfPreMES10>
      <HEAHEA>
        <RefNumHEA4>mrn</RefNumHEA4>
      </HEAHEA>
    </CC015B>

  private def contentAsXml(xml: String): Elem = XML.loadString(xml)

  "DepartureTestMessagesController" - {
    "POST" - {
      "must send a test message to the departures backend and return Created if successful" in {
        val mockDepartureConnector        = mock[DepartureConnector]
        val mockInboundRouterConnector    = mock[InboundRouterConnector]
        val mockDepartureMessageConnector = mock[DepartureMessageConnector]

        when(mockDepartureConnector.getMessages(any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(departureWithMessages)))
        when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
          .thenReturn(
            Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transits-movements-trader-at-departure/movements/departures/1/messages/2")))))
        when(mockDepartureMessageConnector.get(any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(movement)))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector),
            bind[InboundRouterConnector].toInstance(mockInboundRouterConnector),
            bind[DepartureMessageConnector].toInstance(mockDepartureMessageConnector)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

          val result = route(application, request).value

          val xml = contentAsXml((contentAsJson(result) \ "body").as[String])

          status(result) mustEqual CREATED
          (contentAsJson(result) \ "_links" \ "self" \ "href").as[String] mustEqual "/customs/transits/movements/departures/1/messages/2"
          (contentAsJson(result) \ "_links" \ "departure" \ "href").as[String] mustEqual "/customs/transits/movements/departures/1"
          (contentAsJson(result) \ "departureId").as[String] mustEqual "1"
          (contentAsJson(result) \ "messageId").as[String] mustEqual "2"
          (contentAsJson(result) \ "messageType").as[String] mustEqual PositiveAcknowledgement.code
          xml.head.label mustEqual PositiveAcknowledgement.rootNode
        }
      }

      "must return UnsupportedMediaType when no Content-Type specified" in {
        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[ChannelAction].to[FakeChannelAction]
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
            bind[AuthAction].to[FakeAuthAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[ChannelAction].to[FakeChannelAction]
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
            bind[AuthAction].to[FakeAuthAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[ChannelAction].to[FakeChannelAction]
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(invalidRequest)

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
        }
      }

      "must return BadRequest if GET messages in departures backend returns BadRequest" in {
        val mockDepartureConnector        = mock[DepartureConnector]
        val mockInboundRouterConnector    = mock[InboundRouterConnector]
        val mockDepartureMessageConnector = mock[DepartureMessageConnector]

        when(mockDepartureConnector.getMessages(any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(Left(HttpResponse(BAD_REQUEST, "bad_request"))))
        when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
          .thenReturn(
            Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transits-movements-trader-at-departure/movements/departures/1/messages/2")))))
        when(mockDepartureMessageConnector.get(any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(movement)))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector),
            bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual "bad_request"
        }
      }

      "must return BadRequest if POST to inbound router returns BadRequest" in {
        val mockDepartureConnector     = mock[DepartureConnector]
        val mockInboundRouterConnector = mock[InboundRouterConnector]

        when(mockDepartureConnector.getMessages(any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(departureWithMessages)))
        when(mockInboundRouterConnector.post(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "bad_request")))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector),
            bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual "bad_request"
        }
      }

      "must return BadRequest if GET message in departures backend returns BadRequest" in {
        val mockDepartureConnector        = mock[DepartureConnector]
        val mockInboundRouterConnector    = mock[InboundRouterConnector]
        val mockDepartureMessageConnector = mock[DepartureMessageConnector]

        when(mockDepartureConnector.getMessages(any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(departureWithMessages)))
        when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
          .thenReturn(
            Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transits-movements-trader-at-departure/movements/departures/1/messages/2")))))
        when(mockDepartureMessageConnector.get(any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(Left(HttpResponse(BAD_REQUEST, "bad_request"))))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector),
            bind[InboundRouterConnector].toInstance(mockInboundRouterConnector),
            bind[DepartureMessageConnector].toInstance(mockDepartureMessageConnector)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual "bad_request"
        }
      }

      "must return NotFound if GET messages in departures backend returns NotFound" in {
        val mockDepartureConnector        = mock[DepartureConnector]
        val mockInboundRouterConnector    = mock[InboundRouterConnector]
        val mockDepartureMessageConnector = mock[DepartureMessageConnector]

        when(mockDepartureConnector.getMessages(any(), any())(any(), any(), any())).thenReturn(Future.successful(Left(HttpResponse(NOT_FOUND, "text"))))
        when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
          .thenReturn(
            Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transits-movements-trader-at-departure/movements/departures/1/messages/2")))))
        when(mockDepartureMessageConnector.get(any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(Left(HttpResponse(NOT_FOUND, "text2"))))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

          val result = route(application, request).value

          status(result) mustEqual NOT_FOUND
          contentAsString(result) mustBe "text"
        }
      }

      "must return NotFound if GET message in departures backend returns NotFound" in {
        val mockDepartureConnector = mock[DepartureConnector]

        when(mockDepartureConnector.getMessages(any(), any())(any(), any(), any())).thenReturn(Future.successful(Left(HttpResponse(NOT_FOUND, "text"))))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

          val result = route(application, request).value

          status(result) mustEqual NOT_FOUND
          contentAsString(result) mustBe "text"
        }
      }

      "must return InternalServerError if GET messages in departures backend returns InternalServerError" in {
        val mockDepartureConnector        = mock[DepartureConnector]
        val mockInboundRouterConnector    = mock[InboundRouterConnector]
        val mockDepartureMessageConnector = mock[DepartureMessageConnector]

        when(mockDepartureConnector.getMessages(any(), any())(any(), any(), any())).thenReturn(Future.successful(Left(HttpResponse(INTERNAL_SERVER_ERROR, ""))))
        when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
          .thenReturn(
            Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transits-movements-trader-at-departure/movements/departures/1/messages/2")))))
        when(mockDepartureMessageConnector.get(any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(movement)))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector),
            bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR
          contentAsString(result) mustBe empty
        }
      }

      "must return InternalServerError if POST to inbound router returns InternalServerError" in {
        val mockDepartureConnector     = mock[DepartureConnector]
        val mockInboundRouterConnector = mock[InboundRouterConnector]

        when(mockDepartureConnector.getMessages(any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(departureWithMessages)))
        when(mockInboundRouterConnector.post(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector),
            bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR
          contentAsString(result) mustBe empty
        }
      }

      "must return InternalServerError if GET message returns InternalServerError" in {
        val mockDepartureConnector        = mock[DepartureConnector]
        val mockInboundRouterConnector    = mock[InboundRouterConnector]
        val mockDepartureMessageConnector = mock[DepartureMessageConnector]

        when(mockDepartureConnector.getMessages(any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(departureWithMessages)))
        when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
          .thenReturn(
            Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transits-movements-trader-at-departure/movements/departures/1/messages/2")))))
        when(mockDepartureMessageConnector.get(any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(Left(HttpResponse(INTERNAL_SERVER_ERROR, ""))))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector),
            bind[InboundRouterConnector].toInstance(mockInboundRouterConnector),
            bind[DepartureMessageConnector].toInstance(mockDepartureMessageConnector)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR
          contentAsString(result) mustBe empty
        }
      }

      "must return InternalServerError if inbound router returns no location header" in {
        val mockDepartureConnector     = mock[DepartureConnector]
        val mockInboundRouterConnector = mock[InboundRouterConnector]

        when(mockDepartureConnector.getMessages(any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(departureWithMessages)))
        when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(OK, "")))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector),
            bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.injectEISResponse(departureId).url).withJsonBody(exampleRequest)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR
        }
      }
    }

    "submitDepartureDeclaration" - {
      "must submit departure declaration message for the valid input and return the status as ACCEPTED" in {
        val mockDepartureConnector = mock[DepartureConnector]

        when(mockDepartureConnector.createDeclarationMessage(any(), any())(any(), any(), any())).thenReturn(
          Future.successful(HttpResponse(ACCEPTED, "", Map(LOCATION -> Seq("/transits-movements-trader-at-departure/movements/departures/1/messages/1")))))
        val channel = Gen.oneOf(ChannelType.values).sample.value
        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector),
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.submitDepartureDeclaration.url)
            .withHeaders("channel" -> channel.toString)
            .withXmlBody(requestXmlBody.map(trim))

          val result = route(application, request).value

          status(result) mustEqual ACCEPTED
          (contentAsJson(result) \ "_links" \ "self" \ "href").as[String] mustEqual "/customs/transits/movements/departures/1/messages/1"
          (contentAsJson(result) \ "_links" \ "departure" \ "href").as[String] mustEqual "/customs/transits/movements/departures/1"
          (contentAsJson(result) \ "departureId").as[String] mustEqual "1"
          (contentAsJson(result) \ "messageId").as[String] mustEqual "1"
          (contentAsJson(result) \ "messageType").as[String] mustEqual DepartureDeclaration.code
        }
      }

      "must return InternalServerError when 'Location' header is missing in response" in {
        val mockDepartureConnector = mock[DepartureConnector]

        when(mockDepartureConnector.createDeclarationMessage(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(ACCEPTED, "")))
        val channel = Gen.oneOf(ChannelType.values).sample.value
        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector),
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.submitDepartureDeclaration.url)
            .withHeaders("channel" -> channel.toString)
            .withXmlBody(requestXmlBody.map(trim))

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR
        }
      }

      "must return InternalServerError when failed to submit the declaration" in {
        val mockDepartureConnector = mock[DepartureConnector]
        val errorStatus            = Gen.chooseNum(400, 599).sample.value

        when(mockDepartureConnector.createDeclarationMessage(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(errorStatus, "")))
        val channel = Gen.oneOf(ChannelType.values).sample.value
        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[ChannelAction].to[FakeChannelAction],
            bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
            bind[DepartureConnector].toInstance(mockDepartureConnector),
          )
          .build()

        running(application) {
          val request = FakeRequest(POST, routes.DepartureTestMessagesController.submitDepartureDeclaration.url)
            .withHeaders("channel" -> channel.toString)
            .withXmlBody(requestXmlBody.map(trim))

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR
        }
      }

    }
  }
}
