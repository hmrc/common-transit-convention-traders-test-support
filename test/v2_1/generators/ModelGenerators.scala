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

package v2_1.generators

import v2_1.models.CorrelationId
import v2_1.models.EORINumber
import v2_1.models.Message
import v2_1.models.MessageId
import v2_1.models.MessageType
import v2_1.models.Movement
import v2_1.models.MovementId
import v2_1.models.MovementReferenceNumber
import v2_1.models.MovementType
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.Gen.hexChar
import org.scalacheck.Gen.listOfN

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

trait ModelGenerators extends BaseGenerators with JavaTimeGenerators {

  implicit lazy val arbitraryOffsetDateTime: Arbitrary[OffsetDateTime] =
    Arbitrary {
      for {
        timestamp <- Gen.long.map(Instant.ofEpochMilli)
      } yield OffsetDateTime.ofInstant(timestamp, ZoneOffset.UTC)
    }

  implicit lazy val arbitraryMovementId: Arbitrary[MovementId] =
    Arbitrary {
      for {
        id <- Gen.listOfN(16, Gen.hexChar).map(_.mkString)
      } yield MovementId(id)
    }

  implicit lazy val arbitraryMessageId: Arbitrary[MessageId] =
    Arbitrary {
      for {
        id <- Gen.listOfN(16, Gen.hexChar).map(_.mkString)
      } yield MessageId(id)
    }

  implicit lazy val arbitraryMessageType: Arbitrary[MessageType] =
    Arbitrary(Gen.oneOf(MessageType.values))

  implicit val arbitraryMovementType: Arbitrary[MovementType] = Arbitrary {
    Gen.oneOf(MovementType.values)
  }

  implicit val arbitraryEoriNumber: Arbitrary[EORINumber] = Arbitrary {
    for {
      country <- Gen.listOfN(2, Gen.alphaUpperChar)
      numbers <- Gen.listOfN(6, Gen.numChar)
    } yield EORINumber((country ++ numbers).mkString)
  }

  implicit val arbitaryMovementReferenceNumber: Arbitrary[MovementReferenceNumber] = Arbitrary {
    for {
      countryCode <- Gen.listOfN(2, Gen.alphaUpperChar).map(_.mkString)
      randomString <- Gen
        .listOfN(14, Gen.alphaNumChar)
        .map(
          x => x.mkString.toUpperCase
        )
    } yield MovementReferenceNumber(s"21$countryCode$randomString")
  }

  implicit val arbitraryMovement: Arbitrary[Movement] = Arbitrary {
    for {
      id                   <- arbitraryMovementId.arbitrary
      enrollmentEoriNumber <- arbitraryEoriNumber.arbitrary
      movementEoriNumber   <- arbitraryEoriNumber.arbitrary
      mrn                  <- arbitaryMovementReferenceNumber.arbitrary
      created              <- arbitraryOffsetDateTime.arbitrary
      updated              <- arbitraryOffsetDateTime.arbitrary
    } yield Movement(id, enrollmentEoriNumber, movementEoriNumber, Some(mrn), created, updated)
  }

  implicit val arbitraryMessage: Arbitrary[Message] = Arbitrary {
    for {
      id          <- arbitraryMessageId.arbitrary
      time        <- arbitraryOffsetDateTime.arbitrary
      messageType <- arbitraryMessageType.arbitrary
    } yield Message(id, time, messageType, Some(s"<test>body</test>"))
  }

  implicit lazy val arbitraryCorrelationId: Arbitrary[CorrelationId] = Arbitrary {
    for {
      movementId <- listOfN(16, hexChar)
      messageId  <- listOfN(16, hexChar)
    } yield CorrelationId(MovementId(movementId.mkString), MessageId(messageId.mkString))
  }

}
