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

package test.v2_1.generators

import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.Gen.hexChar
import org.scalacheck.Gen.listOfN
import v2_1.models.CorrelationId
import v2_1.models.EORINumber
import v2_1.models.MessageId
import v2_1.models.MessageType
import v2_1.models.MovementId
import v2_1.models.MovementReferenceNumber
import v2_1.models.MovementType

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

trait ItGenerators {

  implicit lazy val arbitraryMovementId: Arbitrary[MovementId] =
    Arbitrary {
      for {
        id <- Gen.listOfN(16, Gen.hexChar).map(_.mkString.toLowerCase)
      } yield MovementId(id)
    }

  implicit lazy val arbitraryMessageId: Arbitrary[MessageId] =
    Arbitrary {
      for {
        id <- Gen.listOfN(16, Gen.hexChar).map(_.mkString.toLowerCase)
      } yield MessageId(id)
    }

  implicit lazy val arbitraryMessageType: Arbitrary[MessageType] =
    Arbitrary {
      Gen.oneOf(MessageType.values)
    }

  implicit lazy val arbitraryMovementType: Arbitrary[MovementType] =
    Arbitrary {
      Gen.oneOf(MovementType.values)
    }

  implicit lazy val arbitraryEoriNumber: Arbitrary[EORINumber] =
    Arbitrary {
      for {
        country <- Gen.listOfN(2, Gen.alphaUpperChar).map(_.mkString)
        number  <- Gen.listOfN(15, Gen.numChar).map(_.mkString)
      } yield EORINumber(s"$country$number")
    }

  implicit val arbitraryMovementReferenceNumber: Arbitrary[MovementReferenceNumber] = Arbitrary {
    for {
      countryCode <- Gen.listOfN(2, Gen.alphaUpperChar).map(_.mkString)
      randomString <- Gen
        .listOfN(14, Gen.alphaNumChar)
        .map(
          x => x.mkString.toUpperCase
        )
    } yield MovementReferenceNumber(s"21$countryCode$randomString")
  }

  implicit lazy val arbitraryOffsetDateTime: Arbitrary[OffsetDateTime] =
    Arbitrary {
      for {
        timestamp <- Gen.long.map(Instant.ofEpochMilli)
      } yield OffsetDateTime.ofInstant(timestamp, ZoneOffset.UTC)
    }

  implicit lazy val arbitraryCorrelationId: Arbitrary[CorrelationId] = Arbitrary {
    for {
      movementId <- listOfN(16, hexChar)
      messageId  <- listOfN(16, hexChar)
    } yield CorrelationId(MovementId(movementId.mkString), MessageId(messageId.mkString))
  }
}
