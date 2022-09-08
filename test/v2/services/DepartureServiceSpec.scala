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
import play.api.test.FakeRequest
import uk.gov.hmrc.http.UpstreamErrorResponse
import v2.base.TestActorSystem.system.dispatcher
import v2.connectors.DepartureConnector
import v2.models.DepartureId
import v2.models.DepartureWithoutMessages
import v2.models.EORINumber
import v2.models.MovementReferenceNumber
import v2.models.errors.PersistenceError

import java.time.OffsetDateTime
import scala.concurrent.Future

class DepartureServiceSpec extends SpecBase {

  implicit val requestHeader = FakeRequest()

  val departureWithoutMessages = DepartureWithoutMessages(
    DepartureId("1"),
    EORINumber("GB121212"),
    EORINumber("GB343434"),
    Some(MovementReferenceNumber("MRN")),
    OffsetDateTime.now(),
    OffsetDateTime.now()
  )

  "DepartureService" - {

    "when retrieving a departure should succeed" in {
      val mockDepartureConnector = mock[DepartureConnector]
      when(mockDepartureConnector.getDeparture(any[String].asInstanceOf[EORINumber], any[String].asInstanceOf[DepartureId])(any(), any(), any()))
        .thenReturn(Future.successful[DepartureWithoutMessages](departureWithoutMessages))

      val departureService = new DepartureServiceImpl(mockDepartureConnector)
      val either           = departureService.getDeparture(EORINumber("GB121212"), DepartureId("1"))

      whenReady(either.value) {
        _.right.map(response => response._id.value mustBe ("1"))
      }
    }

    "when retrieving a departure should fail" in {
      val mockDepartureConnector = mock[DepartureConnector]
      val failedResponse         = UpstreamErrorResponse("", 500)

      when(mockDepartureConnector.getDeparture(any[String].asInstanceOf[EORINumber], any[String].asInstanceOf[DepartureId])(any(), any(), any()))
        .thenReturn(Future.failed(failedResponse))

      val departureService = new DepartureServiceImpl(mockDepartureConnector)
      val either           = departureService.getDeparture(EORINumber("GB121212"), DepartureId("1"))

      whenReady(either.value) {
        _.left.map(response => response mustBe PersistenceError.UnexpectedError(Some(failedResponse)))
      }
    }

  }
}
