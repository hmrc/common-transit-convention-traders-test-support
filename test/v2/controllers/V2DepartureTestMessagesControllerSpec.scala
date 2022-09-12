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
import cats.data.EitherT
import controllers.actions.AuthAction
import controllers.actions.FakeAuthAction
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import v2.generators.ModelGenerators
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.IntegrationPatience
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.HeaderNames
import play.api.http.Status.CREATED
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.http.Status.NOT_FOUND
import play.api.inject.bind
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import play.api.test.Helpers.POST
import play.api.test.Helpers.defaultAwaitTimeout
import play.api.test.Helpers.route
import play.api.test.Helpers.running
import play.api.test.Helpers.status
import routing.VersionedRouting
import v2.models.DepartureId
import v2.models.DepartureWithoutMessages
import v2.models.EORINumber
import v2.models.Message
import v2.models.MessageId
import v2.models.MessageType
import v2.models.MovementReferenceNumber
import v2.models.errors.PersistenceError
import v2.services.DepartureService
import v2.services.InboundRouterService

import java.net.URI
import java.time.OffsetDateTime
import scala.concurrent.Future

class V2DepartureTestMessagesControllerSpec
    extends SpecBase
    with ScalaCheckPropertyChecks
    with ModelGenerators
    with BeforeAndAfterEach
    with IntegrationPatience {

  val departureWithoutMessages = DepartureWithoutMessages(
    DepartureId("1"),
    EORINumber("GB121212"),
    EORINumber("GB343434"),
    Some(MovementReferenceNumber("MRN")),
    OffsetDateTime.now(),
    OffsetDateTime.now()
  )

  val message = Message(
    MessageId("2"),
    OffsetDateTime.now(),
    OffsetDateTime.now(),
    MessageType.PositiveAcknowledgement,
    Some(MessageId("20")),
    Some(new URI("transit-movements")),
    Some("")
  )

  val messageId = MessageId("1")

  val exampleRequest: JsValue = Json.parse(
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
        val mockDepartureService     = mock[DepartureService]
        val mockInboundRouterService = mock[InboundRouterService]

        when(mockDepartureService.getDeparture(any[String].asInstanceOf[EORINumber], any[String].asInstanceOf[DepartureId])(any(), any(), any()))
          .thenReturn(EitherT[Future, PersistenceError, DepartureWithoutMessages](Future.successful(Right(departureWithoutMessages))))

        when(mockInboundRouterService.post(any(), any[String], any[String].asInstanceOf[DepartureId])(any(), any()))
          .thenReturn(EitherT[Future, PersistenceError, MessageId](Future.successful(Right(messageId))))

        when(
          mockDepartureService
            .getMessage(any[String].asInstanceOf[EORINumber], any[String].asInstanceOf[DepartureId], any[String].asInstanceOf[MessageId])(any(), any(), any()))
          .thenReturn(EitherT[Future, PersistenceError, Message](Future.successful(Right(message))))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[DepartureService].toInstance(mockDepartureService),
            bind[InboundRouterService].toInstance(mockInboundRouterService)
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
          status(result) mustEqual CREATED
        }
      }

      "must send a test message to the departures backend and return Not found if no matching departure" in {
        val mockDepartureService = mock[DepartureService]

        when(mockDepartureService.getDeparture(any[String].asInstanceOf[EORINumber], any[String].asInstanceOf[DepartureId])(any(), any(), any()))
          .thenReturn(
            EitherT[Future, PersistenceError, DepartureWithoutMessages](Future.successful(Left(PersistenceError.DepartureNotFound(DepartureId("1"))))))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[DepartureService].toInstance(mockDepartureService),
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
          status(result) mustEqual NOT_FOUND
        }
      }

      "must send a test message to the departures backend and return inbound router error" in {
        val mockDepartureService     = mock[DepartureService]
        val mockInboundRouterService = mock[InboundRouterService]

        when(mockDepartureService.getDeparture(any[String].asInstanceOf[EORINumber], any[String].asInstanceOf[DepartureId])(any(), any(), any()))
          .thenReturn(EitherT[Future, PersistenceError, DepartureWithoutMessages](Future.successful(Right(departureWithoutMessages))))

        when(mockInboundRouterService.post(any[MessageType], any[String], any[String].asInstanceOf[DepartureId])(any(), any()))
          .thenReturn(EitherT[Future, PersistenceError, MessageId](Future.successful(Left(PersistenceError.UnexpectedError()))))

        val application = baseApplicationBuilder
          .overrides(
            bind[AuthAction].to[FakeAuthAction],
            bind[DepartureService].toInstance(mockDepartureService),
            bind[InboundRouterService].toInstance(mockInboundRouterService)
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
          status(result) mustEqual INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
