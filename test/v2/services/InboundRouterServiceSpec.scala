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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status.CREATED
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HttpResponse
import v2.connectors.InboundRouterConnector
import v2.models.DepartureId
import v2.models.DepartureWithoutMessages
import v2.models.EORINumber
import v2.models.MessageType
import v2.models.MovementReferenceNumber
import v2.models.errors.PersistenceError

import java.time.OffsetDateTime
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

class InboundRouterServiceSpec extends SpecBase {

  implicit val requestHeader = FakeRequest()

  val departureWithoutMessages = DepartureWithoutMessages(
    DepartureId("1"),
    EORINumber("GB121212"),
    EORINumber("GB343434"),
    Some(MovementReferenceNumber("MRN")),
    OffsetDateTime.now(),
    OffsetDateTime.now()
  )

  "InboundRouterService" - {

    "when posting a message, should succeed and respond with the message Id" in {
      val inboundRouterConnector = mock[InboundRouterConnector]
      val response               = HttpResponse(CREATED, "Created", Map("Location" -> Seq("3")))

      when(inboundRouterConnector.post(any[MessageType], any(), any[String].asInstanceOf[DepartureId])(any(), any()))
        .thenReturn(Future.successful[HttpResponse](response))

      val inboundRouterService = new InboundRouterServiceImpl(inboundRouterConnector)

      val either = inboundRouterService.post(MessageType.PositiveAcknowledgement, "msg", DepartureId("1"))

      whenReady(either.value) {
        _.right.map(response => response.value mustBe ("3"))
      }
    }

    "when posting a message, should fail if connector cannot complete its write" in {
      val inboundRouterConnector = mock[InboundRouterConnector]
      val response               = HttpResponse(INTERNAL_SERVER_ERROR, "Error")

      when(inboundRouterConnector.post(any[MessageType], any(), any[String].asInstanceOf[DepartureId])(any(), any()))
        .thenReturn(Future.successful[HttpResponse](response))

      val inboundRouterService = new InboundRouterServiceImpl(inboundRouterConnector)

      val either = inboundRouterService.post(MessageType.PositiveAcknowledgement, "msg", DepartureId("1"))

      whenReady(either.value) {
        _.left.map(response => response mustBe PersistenceError.UnexpectedError(None))
      }
    }

  }
}
