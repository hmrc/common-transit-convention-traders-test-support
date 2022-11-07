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
import cats.syntax.all._
import org.scalacheck.Arbitrary.arbitrary
import v2.generators.ModelGenerators
import v2.models.MessageType
import v2.models.MovementId
import v2.models.errors.MessageGenerationError
import v2.models.errors.PersistenceError
import v2.models.errors.PresentationError

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ErrorTranslatorSpec extends SpecBase with ModelGenerators {

  object Harness extends ErrorTranslator

  import Harness._

  "ErrorConverter#asPresentation" - {

    case object DummyError

    implicit val dummyErrorConversion = new Converter[DummyError.type] {
      override def convert(input: DummyError.type): PresentationError = PresentationError.badRequestError("dummy")
    }

    "for a success returns the same right" in {
      val input = Right[DummyError.type, Unit](()).toEitherT[Future]
      whenReady(input.asPresentation.value) {
        _ mustBe Right(())
      }
    }

    "for an error returns a left with the appropriate presentation error" in {
      val input = Left[DummyError.type, Unit](DummyError).toEitherT[Future]
      whenReady(input.asPresentation.value) {
        _ mustBe Left(PresentationError.badRequestError("dummy"))
      }
    }
  }

  "PersistenceError" - {

    "an Unexpected Error with no exception returns an internal service error with no exception" in {
      val input  = PersistenceError.UnexpectedError(None)
      val output = PresentationError.internalServiceError()

      Harness.persistenceErrorConverter.convert(input) mustBe output
    }

    "an Unexpected Error with an exception returns an internal service error with an exception" in {
      val exception = new IllegalStateException()
      val input     = PersistenceError.UnexpectedError(Some(exception))
      val output    = PresentationError.internalServiceError(cause = Some(exception))

      Harness.persistenceErrorConverter.convert(input) mustBe output
    }

    "a MovementNotFound error returns a not found error" in forAll(arbitrary[MovementId]) {
      movementId =>
        val input  = PersistenceError.MovementNotFound(movementId)
        val output = PresentationError.notFoundError(s"Movement with ID ${movementId.value} was not found")

        Harness.persistenceErrorConverter.convert(input) mustBe output
    }

  }

  "MessageGenerationError" - {

    "a MessageTypeNotSupported returns a bad request error" in forAll(arbitrary[MessageType]) {
      messageType =>
        val input  = MessageGenerationError.MessageTypeNotSupported(messageType)
        val output = PresentationError.notImplementedError(s"Message type ${messageType.code} is not supported for this movement type")

        Harness.messageGenerationErrorConverter.convert(input) mustBe output
    }

  }

}
