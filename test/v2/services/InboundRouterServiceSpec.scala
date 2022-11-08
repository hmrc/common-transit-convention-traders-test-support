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

package v2.services

import base.SpecBase
import config.Constants.MessageIdHeaderKey
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito.when
import play.api.http.Status.CREATED
import play.api.http.Status.INTERNAL_SERVER_ERROR
import uk.gov.hmrc.http.HttpResponse
import v2.connectors.InboundRouterConnector
import v2.models.MovementId
import v2.models.Movement
import v2.models.EORINumber
import v2.models.MessageType
import v2.models.MovementReferenceNumber
import v2.models.WrappedXMLMessage
import v2.models.XMLMessage
import v2.models.errors.RouterError

import java.time.OffsetDateTime
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

class InboundRouterServiceSpec extends SpecBase {

  val departureWithoutMessages = Movement(
    MovementId("1"),
    EORINumber("GB121212"),
    EORINumber("GB343434"),
    Some(MovementReferenceNumber("MRN")),
    OffsetDateTime.now(),
    OffsetDateTime.now()
  )

  "InboundRouterService" - {

    "when posting a message, should succeed and respond with the message Id" in {
      val inboundRouterConnector = mock[InboundRouterConnector]
      val response               = HttpResponse(CREATED, "Created", Map(MessageIdHeaderKey -> Seq("3")))
      val wrappedMessage         = <TraderChannelResponse><msg></msg></TraderChannelResponse>

      when(inboundRouterConnector.post(any[MessageType], WrappedXMLMessage(eqTo(wrappedMessage)), any[String].asInstanceOf[MovementId])(any(), any()))
        .thenReturn(Future.successful[HttpResponse](response))

      val inboundRouterService = new InboundRouterServiceImpl(inboundRouterConnector)

      val either = inboundRouterService.post(MessageType.PositiveAcknowledgement, XMLMessage(<msg></msg>), MovementId("1"))

      whenReady(either.value) {
        _.right.map(response => response.value mustBe ("3"))
      }
    }

    "when posting a message, should fail if location header missing" in {
      val inboundRouterConnector = mock[InboundRouterConnector]
      val response               = HttpResponse(CREATED, "Created")
      val wrappedXMLMessage      = <TraderChannelResponse><msg></msg></TraderChannelResponse>

      when(inboundRouterConnector.post(any[MessageType], WrappedXMLMessage(eqTo(wrappedXMLMessage)), any[String].asInstanceOf[MovementId])(any(), any()))
        .thenReturn(Future.successful[HttpResponse](response))

      val inboundRouterService = new InboundRouterServiceImpl(inboundRouterConnector)

      val either = inboundRouterService.post(MessageType.PositiveAcknowledgement, XMLMessage(<msg></msg>), MovementId("1"))

      whenReady(either.value) {
        _.left.map(response =>
          response.leftSideValue match {
            case RouterError.Unexpected(Some(thr)) => thr.getMessage mustBe "X-Message-Id header missing from router response"
            case _                                 => fail("Expected a different error")
        })
      }
    }

    "when posting a message, should fail if connector cannot complete its write" in {
      val inboundRouterConnector = mock[InboundRouterConnector]
      val response               = HttpResponse(INTERNAL_SERVER_ERROR, "Error")
      val wrappedXMLMessage      = <TraderChannelResponse><msg></msg></TraderChannelResponse>

      when(inboundRouterConnector.post(any[MessageType], WrappedXMLMessage(eqTo(wrappedXMLMessage)), any[String].asInstanceOf[MovementId])(any(), any()))
        .thenReturn(Future.successful[HttpResponse](response))

      val inboundRouterService = new InboundRouterServiceImpl(inboundRouterConnector)

      val either = inboundRouterService.post(MessageType.PositiveAcknowledgement, XMLMessage(<msg></msg>), MovementId("1"))

      whenReady(either.value) {
        _.left.map(response => response mustBe RouterError.Unexpected(None))
      }
    }

  }
}
