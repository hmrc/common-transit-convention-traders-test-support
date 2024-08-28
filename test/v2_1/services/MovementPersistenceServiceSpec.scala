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

package v2_1.services

import base.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import play.api.test.FakeRequest
import uk.gov.hmrc.http.UpstreamErrorResponse
import v2_1.base.TestActorSystem.system.dispatcher
import v2_1.connectors.MovementConnector
import v2_1.generators.ModelGenerators
import v2_1.models.MovementId
import v2_1.models.Movement
import v2_1.models.EORINumber
import v2_1.models.MovementType
import v2_1.models.errors.PersistenceError

import scala.concurrent.Future

class MovementPersistenceServiceSpec extends SpecBase with ModelGenerators {

  implicit val requestHeader = FakeRequest()

  "DepartureService" - {

    "when retrieving a departure should succeed" in forAll(arbitrary[MovementType], arbitrary[Movement]) {
      (movementType, movement) =>
        val mockDepartureConnector = mock[MovementConnector]
        when(
          mockDepartureConnector
            .getMovement(eqTo(movementType), EORINumber(eqTo(movement.enrollmentEORINumber.value)), MovementId(eqTo(movement._id.value)))(any(), any())
        )
          .thenReturn(Future.successful[Movement](movement))

        val departureService = new MovementPersistenceServiceImpl(mockDepartureConnector)
        val either           = departureService.getMovement(movementType, movement.enrollmentEORINumber, movement._id)

        whenReady(either.value) {
          r =>
            r mustBe Right(movement)
        }
    }

    "when retrieving a departure should fail" in forAll(arbitrary[MovementType], arbitrary[Movement]) {
      (movementType, movement) =>
        val mockDepartureConnector = mock[MovementConnector]
        val failedResponse         = UpstreamErrorResponse("", 500)

        when(
          mockDepartureConnector
            .getMovement(eqTo(movementType), EORINumber(eqTo(movement.enrollmentEORINumber.value)), MovementId(eqTo(movement._id.value)))(any(), any())
        )
          .thenReturn(Future.failed(failedResponse))

        val departureService = new MovementPersistenceServiceImpl(mockDepartureConnector)
        val either           = departureService.getMovement(movementType, movement.enrollmentEORINumber, movement._id)

        whenReady(either.value) {
          r =>
            r mustBe Left(PersistenceError.Unexpected(Some(failedResponse)))
        }
    }

  }
}
