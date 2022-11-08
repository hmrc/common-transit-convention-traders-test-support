package v2.generators

import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import v2.models.EORINumber
import v2.models.MessageId
import v2.models.MessageType
import v2.models.MovementId
import v2.models.MovementReferenceNumber
import v2.models.MovementType

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

trait ItGenerators {

  implicit lazy val arbitraryMovementId: Arbitrary[MovementId] = {
    Arbitrary {
      for {
        id <- Gen.listOfN(16, Gen.hexChar).map(_.mkString.toLowerCase)
      } yield MovementId(id)
    }
  }

  implicit lazy val arbitraryMessageId: Arbitrary[MessageId] = {
    Arbitrary {
      for {
        id <- Gen.listOfN(16, Gen.hexChar).map(_.mkString.toLowerCase)
      } yield MessageId(id)
    }
  }

  implicit lazy val arbitraryMessageType: Arbitrary[MessageType] = {
    Arbitrary {
      Gen.oneOf(MessageType.values)
    }
  }

  implicit lazy val arbitraryMovementType: Arbitrary[MovementType] = {
    Arbitrary {
      Gen.oneOf(MovementType.values)
    }
  }

  implicit lazy val arbitraryEoriNumber: Arbitrary[EORINumber] = {
    Arbitrary {
      for {
        country <- Gen.listOfN(2, Gen.alphaUpperChar).map(_.mkString)
        number  <- Gen.listOfN(15, Gen.numChar).map(_.mkString)
      } yield EORINumber(s"$country$number")
    }
  }

  implicit val arbitaryMovementReferenceNumber: Arbitrary[MovementReferenceNumber] = Arbitrary {
    for {
      countryCode <- Gen.listOfN(2, Gen.alphaUpperChar).map(_.mkString)
      randomString <- Gen.listOfN(14, Gen.alphaNumChar).map(x => x.mkString.toUpperCase)
    } yield MovementReferenceNumber(s"21$countryCode$randomString")
  }

  implicit lazy val arbitraryOffsetDateTime: Arbitrary[OffsetDateTime] =
    Arbitrary {
      for {
        timestamp <- Gen.long.map(Instant.ofEpochMilli)
      } yield OffsetDateTime.ofInstant(timestamp, ZoneOffset.UTC)
    }

}
