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

package services

import base.SpecBase
import models.MessageType
import models.MovementId
import models.MovementType
import models.XMLMessage
import models.errors.MessageGenerationError
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import versioned.v2_1.generators.ArrivalMessageGenerator
import versioned.v2_1.generators.DepartureMessageGenerator
import versioned.v2_1.generators.ModelGenerators

import scala.concurrent.ExecutionContext.Implicits.global

class MessageGenerationServiceSpec extends SpecBase with ModelGenerators {

  "generators Partial Function" - {
    val arrivalMessageGenerator   = mock[ArrivalMessageGenerator]
    val departureMessageGenerator = mock[DepartureMessageGenerator]

    val sut = new MessageGenerationService(arrivalMessageGenerator, departureMessageGenerator)

    "a movement type of Arrival will return the arrival generator" in {
      sut.generators(MovementType.Arrival) mustBe arrivalMessageGenerator
    }

    "a movement type of Departure will return the departure generator" in {
      sut.generators(MovementType.Departure) mustBe departureMessageGenerator
    }

  }

  "generateMessage" - {

    val arrivalMessageGenerator   = mock[ArrivalMessageGenerator]
    val departureMessageGenerator = mock[DepartureMessageGenerator]

    val validArrivalMessage   = XMLMessage(<arrival></arrival>)
    val validDepartureMessage = XMLMessage(<departure></departure>)

    val arrivalPF: PartialFunction[MessageType, XMLMessage] = {
      case MessageType.GoodsReleaseNotification => validArrivalMessage
    }

    val departurePF: PartialFunction[MessageType, XMLMessage] = {
      case MessageType.PositiveAcknowledgement => validDepartureMessage
    }

    when(arrivalMessageGenerator.generate(MovementId(anyString()))).thenReturn(arrivalPF)

    when(departureMessageGenerator.generate(MovementId(anyString()))).thenReturn(departurePF)

    val sut = new MessageGenerationService(arrivalMessageGenerator, departureMessageGenerator)

    "a valid message type for a departure returns a right with an XMLMessage" in forAll(arbitrary[MovementId]) {
      movementId =>
        whenReady(sut.generateMessage(MessageType.PositiveAcknowledgement, MovementType.Departure, movementId).value) {
          result =>
            result mustBe Right(validDepartureMessage)
        }
    }

    "a valid message type for a arrival returns a right with an XMLMessage" in forAll(arbitrary[MovementId]) {
      movementId =>
        whenReady(sut.generateMessage(MessageType.GoodsReleaseNotification, MovementType.Arrival, movementId).value) {
          result =>
            result mustBe Right(validArrivalMessage)
        }
    }

    "a valid but incorrect message type for a arrival returns a left with a MessageTypeNotSupported" in forAll(arbitrary[MovementId]) {
      movementId =>
        whenReady(sut.generateMessage(MessageType.PositiveAcknowledgement, MovementType.Arrival, movementId).value) {
          result =>
            result mustBe Left(MessageGenerationError.MessageTypeNotSupported(MessageType.PositiveAcknowledgement))
        }
    }

    "a valid but incorrect message type for a departure returns a left with a MessageTypeNotSupported" in forAll(arbitrary[MovementId]) {
      movementId =>
        whenReady(sut.generateMessage(MessageType.GoodsReleaseNotification, MovementType.Departure, movementId).value) {
          result =>
            result mustBe Left(MessageGenerationError.MessageTypeNotSupported(MessageType.GoodsReleaseNotification))
        }
    }

  }

}
