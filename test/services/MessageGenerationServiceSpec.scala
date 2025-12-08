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
import models.*
import models.errors.MessageGenerationError
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import versioned.v2_1.generators.ModelGenerators

import scala.concurrent.ExecutionContext.Implicits.global

class MessageGenerationServiceSpec extends SpecBase with ModelGenerators {

  "generators Partial Function" - {
    val v2ArrivalMessageGenerator   = mock[versioned.v2_1.generators.ArrivalMessageGenerator]
    val v2DepartureMessageGenerator = mock[versioned.v2_1.generators.DepartureMessageGenerator]
    val v3ArrivalMessageGenerator   = mock[versioned.v3_0.generators.ArrivalMessageGenerator]
    val v3DepartureMessageGenerator = mock[versioned.v3_0.generators.DepartureMessageGenerator]

    val sut = new MessageGenerationService(v2ArrivalMessageGenerator, v2DepartureMessageGenerator, v3ArrivalMessageGenerator, v3DepartureMessageGenerator)

    "a movement type of Arrival will return the v2 arrival generator" in {
      sut.generators(VersionHeader.v2_1, MovementType.Arrival) mustBe v2ArrivalMessageGenerator
    }

    "a movement type of Departure will return the v2 departure generator" in {
      sut.generators(VersionHeader.v2_1, MovementType.Departure) mustBe v2DepartureMessageGenerator
    }

    "a movement type of Arrival will return the v3 arrival generator" in {
      sut.generators(VersionHeader.v3_0, MovementType.Arrival) mustBe v3ArrivalMessageGenerator
    }

    "a movement type of Departure will return the v3 departure generator" in {
      sut.generators(VersionHeader.v3_0, MovementType.Departure) mustBe v3DepartureMessageGenerator
    }

  }

  "generateMessage" - {

    val v2ArrivalMessageGenerator   = mock[versioned.v2_1.generators.ArrivalMessageGenerator]
    val v2DepartureMessageGenerator = mock[versioned.v2_1.generators.DepartureMessageGenerator]

    val v3ArrivalMessageGenerator   = mock[versioned.v3_0.generators.ArrivalMessageGenerator]
    val v3DepartureMessageGenerator = mock[versioned.v3_0.generators.DepartureMessageGenerator]

    val validArrivalMessage   = XMLMessage(<arrival></arrival>)
    val validDepartureMessage = XMLMessage(<departure></departure>)

    val arrivalPF: PartialFunction[MessageType, XMLMessage] = {
      case MessageType.GoodsReleaseNotification => validArrivalMessage
    }

    val departurePF: PartialFunction[MessageType, XMLMessage] = {
      case MessageType.PositiveAcknowledgement => validDepartureMessage
    }

    when(v2ArrivalMessageGenerator.generate(MovementId(anyString()))).thenReturn(arrivalPF)

    when(v2DepartureMessageGenerator.generate(MovementId(anyString()))).thenReturn(departurePF)

    val sut = new MessageGenerationService(v2ArrivalMessageGenerator, v2DepartureMessageGenerator, v3ArrivalMessageGenerator, v3DepartureMessageGenerator)

    "a valid message type for a departure returns a right with an XMLMessage" in forAll(arbitrary[MovementId]) {
      movementId =>
        whenReady(sut.generateMessage(MessageType.PositiveAcknowledgement, MovementType.Departure, movementId, VersionHeader.v2_1).value) {
          result =>
            result mustBe Right(validDepartureMessage)
        }
    }

    "a valid message type for a arrival returns a right with an XMLMessage" in forAll(arbitrary[MovementId]) {
      movementId =>
        whenReady(sut.generateMessage(MessageType.GoodsReleaseNotification, MovementType.Arrival, movementId, VersionHeader.v2_1).value) {
          result =>
            result mustBe Right(validArrivalMessage)
        }
    }

    "a valid but incorrect message type for a arrival returns a left with a MessageTypeNotSupported" in forAll(arbitrary[MovementId]) {
      movementId =>
        whenReady(sut.generateMessage(MessageType.PositiveAcknowledgement, MovementType.Arrival, movementId, VersionHeader.v2_1).value) {
          result =>
            result mustBe Left(MessageGenerationError.MessageTypeNotSupported(MessageType.PositiveAcknowledgement))
        }
    }

    "a valid but incorrect message type for a departure returns a left with a MessageTypeNotSupported" in forAll(arbitrary[MovementId]) {
      movementId =>
        whenReady(sut.generateMessage(MessageType.GoodsReleaseNotification, MovementType.Departure, movementId, VersionHeader.v2_1).value) {
          result =>
            result mustBe Left(MessageGenerationError.MessageTypeNotSupported(MessageType.GoodsReleaseNotification))
        }
    }

  }

}
