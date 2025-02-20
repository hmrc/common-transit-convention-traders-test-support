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

package controllers

import base.SpecBase
import config.Constants
import connectors.ArrivalConnector
import connectors.ArrivalMessageConnector
import connectors.InboundRouterConnector
import controllers.actions.AuthAction
import controllers.actions.ChannelAction
import controllers.actions.FakeAuthAction
import controllers.actions.FakeChannelAction
import controllers.actions.FakeVersionOneEnabledCheckAction
import controllers.actions.VersionOneEnabledCheckAction
import generators.ModelGenerators
import models.ArrivalId
import models.MessageType.ArrivalRejection
import models.domain.MovementMessage
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.IntegrationPatience
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.HeaderNames
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.AnyContentAsXml
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import play.api.test.Helpers.POST
import play.api.test.Helpers.route
import play.api.test.Helpers.running
import play.api.test.Helpers.status
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import v2_1.models.MessageId

import java.time.LocalDateTime
import scala.concurrent.Future
import scala.xml.Elem
import scala.xml.XML

class ArrivalTestMessagesControllerSpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators with BeforeAndAfterEach with IntegrationPatience {

  val arrivalId = new ArrivalId(1)
  val messageId = MessageId(Constants.DefaultTriggerId)

  val movement = MovementMessage("/transit-movements-trader-at-destination/movements/arrivals/1/messages/2", LocalDateTime.now, "abc", <test>default</test>)

  val exampleRequest = Json.parse(
    """{
      |     "message": {
      |         "messageType": "IE008"
      |     }
      | }""".stripMargin
  )

  private def contentAsXml(xml: String): Elem = XML.loadString(xml)

  "POST /movements/arrivals/:arrivalId/messages" - {
    "must send a test message to the arrivals backend and return Created if successful" in {
      val mockArrivalConnector        = mock[ArrivalConnector]
      val mockInboundRouterConnector  = mock[InboundRouterConnector]
      val mockArrivalMessageConnector = mock[ArrivalMessageConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transit-movements-trader-at-destination/movements/arrivals/1/messages/2")))))
      when(mockArrivalMessageConnector.get(any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(movement)))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector),
          bind[ArrivalMessageConnector].toInstance(mockArrivalMessageConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        val xml = contentAsXml((contentAsJson(result) \ "body").as[String])

        status(result) mustEqual CREATED
        (contentAsJson(result) \ "_links" \ "self" \ "href").as[String] mustEqual "/customs/transits/movements/arrivals/1/messages/2"
        (contentAsJson(result) \ "_links" \ "arrival" \ "href").as[String] mustEqual "/customs/transits/movements/arrivals/1"
        (contentAsJson(result) \ "arrivalId").as[String] mustEqual "1"
        (contentAsJson(result) \ "messageId").as[String] mustEqual "2"
        (contentAsJson(result) \ "messageType").as[String] mustEqual ArrivalRejection.code
        xml.head.label mustEqual ArrivalRejection.rootNode
      }
    }

    "must return UnsupportedMediaType when no Content-Type specified" in {
      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction]
        )
        .build()

      running(application) {
        val request = FakeRequest(
          method = POST,
          uri = routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url,
          headers = FakeHeaders(Nil),
          body = AnyContentAsEmpty
        )

        val result = route(application, request).value

        status(result) mustEqual UNSUPPORTED_MEDIA_TYPE
      }
    }

    "must return UnsupportedMediaType when invalid Content-Type specified" in {
      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction]
        )
        .build()

      running(application) {
        val request = FakeRequest(
          method = POST,
          uri = routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url,
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
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction]
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url).withJsonBody(invalidRequest)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    "must return BadRequest if arrivals backend returns BadRequest" in {
      val mockArrivalConnector       = mock[ArrivalConnector]
      val mockInboundRouterConnector = mock[InboundRouterConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    "must return BadRequest if GET message returns BadRequest" in {
      val mockArrivalConnector        = mock[ArrivalConnector]
      val mockInboundRouterConnector  = mock[InboundRouterConnector]
      val mockArrivalMessageConnector = mock[ArrivalMessageConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transit-movements-trader-at-destination/movements/arrivals/1/messages/2")))))
      when(mockArrivalMessageConnector.get(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(HttpResponse(BAD_REQUEST, "bad_request"))))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector),
          bind[ArrivalMessageConnector].toInstance(mockArrivalMessageConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual "bad_request"
      }
    }

    "must return InternalServerError if arrivals backend returns InternalServerError" in {
      val mockArrivalConnector       = mock[ArrivalConnector]
      val mockInboundRouterConnector = mock[InboundRouterConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "must return NotFound if arrivals backend returns NotFound" in {
      val mockArrivalConnector = mock[ArrivalConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(NOT_FOUND, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual NOT_FOUND
      }
    }

    "must return InternalServerError if GET fails in arrival id check" in {
      val mockArrivalConnector = mock[ArrivalConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.failed(new Exception("failed")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "must return InternalServerError if POST fails to send to arrivals backend" in {
      val mockArrivalConnector       = mock[ArrivalConnector]
      val mockInboundRouterConnector = mock[InboundRouterConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any())).thenReturn(Future.failed(new Exception("failed")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "must return InternalServerError if inbound router returns no location header" in {
      val mockArrivalConnector       = mock[ArrivalConnector]
      val mockInboundRouterConnector = mock[InboundRouterConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "must return InternalServerError if GET message returns InternalServerError" in {
      val mockArrivalConnector        = mock[ArrivalConnector]
      val mockInboundRouterConnector  = mock[InboundRouterConnector]
      val mockArrivalMessageConnector = mock[ArrivalMessageConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transit-movements-trader-at-destination/movements/arrivals/1/messages/2")))))
      when(mockArrivalMessageConnector.get(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(HttpResponse(INTERNAL_SERVER_ERROR, ""))))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector),
          bind[ArrivalMessageConnector].toInstance(mockArrivalMessageConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "must return InternalServerError if GET message operation fails" in {
      val mockArrivalConnector        = mock[ArrivalConnector]
      val mockInboundRouterConnector  = mock[InboundRouterConnector]
      val mockArrivalMessageConnector = mock[ArrivalMessageConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transit-movements-trader-at-destination/movements/arrivals/1/messages/2")))))
      when(mockArrivalMessageConnector.get(any(), any(), any())(any(), any(), any())).thenReturn(Future.failed(new Exception("failed")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector),
          bind[ArrivalMessageConnector].toInstance(mockArrivalMessageConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponse(arrivalId.index.toString).url).withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }
  }

  "POST /movements/arrivals/:arrivalId/messages/:messageId" - {
    "must send a test message to the arrivals backend and return Created if successful" in {
      val mockArrivalConnector        = mock[ArrivalConnector]
      val mockInboundRouterConnector  = mock[InboundRouterConnector]
      val mockArrivalMessageConnector = mock[ArrivalMessageConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transit-movements-trader-at-destination/movements/arrivals/1/messages/2")))))
      when(mockArrivalMessageConnector.get(any(), any(), any())(any(), any(), any())).thenReturn(Future.successful(Right(movement)))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector),
          bind[ArrivalMessageConnector].toInstance(mockArrivalMessageConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url)
          .withJsonBody(exampleRequest)

        val result = route(application, request).value

        val xml = contentAsXml((contentAsJson(result) \ "body").as[String])

        status(result) mustEqual CREATED
        (contentAsJson(result) \ "_links" \ "self" \ "href").as[String] mustEqual "/customs/transits/movements/arrivals/1/messages/2"
        (contentAsJson(result) \ "_links" \ "arrival" \ "href").as[String] mustEqual "/customs/transits/movements/arrivals/1"
        (contentAsJson(result) \ "arrivalId").as[String] mustEqual "1"
        (contentAsJson(result) \ "messageId").as[String] mustEqual "2"
        (contentAsJson(result) \ "messageType").as[String] mustEqual ArrivalRejection.code
        xml.head.label mustEqual ArrivalRejection.rootNode
      }
    }

    "must return UnsupportedMediaType when no Content-Type specified" in {
      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction]
        )
        .build()

      running(application) {
        val request = FakeRequest(
          method = POST,
          uri = routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url,
          headers = FakeHeaders(Nil),
          body = AnyContentAsEmpty
        )

        val result = route(application, request).value

        status(result) mustEqual UNSUPPORTED_MEDIA_TYPE
      }
    }

    "must return UnsupportedMediaType when invalid Content-Type specified" in {
      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction]
        )
        .build()

      running(application) {
        val request = FakeRequest(
          method = POST,
          uri = routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url,
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
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction]
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url)
          .withJsonBody(invalidRequest)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    "must return BadRequest if arrivals backend returns BadRequest" in {
      val mockArrivalConnector       = mock[ArrivalConnector]
      val mockInboundRouterConnector = mock[InboundRouterConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url)
          .withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    "must return BadRequest if GET message returns BadRequest" in {
      val mockArrivalConnector        = mock[ArrivalConnector]
      val mockInboundRouterConnector  = mock[InboundRouterConnector]
      val mockArrivalMessageConnector = mock[ArrivalMessageConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transit-movements-trader-at-destination/movements/arrivals/1/messages/2")))))
      when(mockArrivalMessageConnector.get(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(HttpResponse(BAD_REQUEST, "bad_request"))))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector),
          bind[ArrivalMessageConnector].toInstance(mockArrivalMessageConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url)
          .withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual "bad_request"
      }
    }

    "must return InternalServerError if arrivals backend returns InternalServerError" in {
      val mockArrivalConnector       = mock[ArrivalConnector]
      val mockInboundRouterConnector = mock[InboundRouterConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url)
          .withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "must return NotFound if arrivals backend returns NotFound" in {
      val mockArrivalConnector = mock[ArrivalConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(NOT_FOUND, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url)
          .withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual NOT_FOUND
      }
    }

    "must return InternalServerError if GET fails in arrival id check" in {
      val mockArrivalConnector = mock[ArrivalConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.failed(new Exception("failed")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url)
          .withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "must return InternalServerError if POST fails to send to arrivals backend" in {
      val mockArrivalConnector       = mock[ArrivalConnector]
      val mockInboundRouterConnector = mock[InboundRouterConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any())).thenReturn(Future.failed(new Exception("failed")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url)
          .withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "must return InternalServerError if inbound router returns no location header" in {
      val mockArrivalConnector       = mock[ArrivalConnector]
      val mockInboundRouterConnector = mock[InboundRouterConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url)
          .withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "must return InternalServerError if GET message returns InternalServerError" in {
      val mockArrivalConnector        = mock[ArrivalConnector]
      val mockInboundRouterConnector  = mock[InboundRouterConnector]
      val mockArrivalMessageConnector = mock[ArrivalMessageConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transit-movements-trader-at-destination/movements/arrivals/1/messages/2")))))
      when(mockArrivalMessageConnector.get(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(HttpResponse(INTERNAL_SERVER_ERROR, ""))))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector),
          bind[ArrivalMessageConnector].toInstance(mockArrivalMessageConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url)
          .withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "must return InternalServerError if GET message operation fails" in {
      val mockArrivalConnector        = mock[ArrivalConnector]
      val mockInboundRouterConnector  = mock[InboundRouterConnector]
      val mockArrivalMessageConnector = mock[ArrivalMessageConnector]

      when(mockArrivalConnector.get(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))
      when(mockInboundRouterConnector.post(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "", Map(LOCATION -> Seq("/transit-movements-trader-at-destination/movements/arrivals/1/messages/2")))))
      when(mockArrivalMessageConnector.get(any(), any(), any())(any(), any(), any())).thenReturn(Future.failed(new Exception("failed")))

      val application = baseApplicationBuilder
        .overrides(
          bind[AuthAction].to[FakeAuthAction],
          bind[ChannelAction].to[FakeChannelAction],
          bind[VersionOneEnabledCheckAction].to[FakeVersionOneEnabledCheckAction],
          bind[ArrivalConnector].toInstance(mockArrivalConnector),
          bind[InboundRouterConnector].toInstance(mockInboundRouterConnector),
          bind[ArrivalMessageConnector].toInstance(mockArrivalMessageConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routing.routes.ArrivalsRouter.injectEISResponseWithTriggerId(arrivalId.index.toString, messageId.value).url)
          .withJsonBody(exampleRequest)

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }
  }

}
