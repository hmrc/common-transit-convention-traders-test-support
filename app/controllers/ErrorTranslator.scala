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

package controllers

import cats.data.EitherT
import models.errors.MessageGenerationError
import models.errors.PersistenceError
import models.errors.PresentationError
import models.errors.RouterError

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

trait ErrorTranslator {

  implicit class ErrorConverter[E, A](value: EitherT[Future, E, A]) {

    def asPresentation(implicit c: Converter[E], ec: ExecutionContext): EitherT[Future, PresentationError, A] =
      value.leftMap(c.convert)
  }

  trait Converter[E] {
    def convert(input: E): PresentationError
  }

  implicit val persistenceErrorConverter: Converter[PersistenceError] = {
    case PersistenceError.MovementNotFound(movementType, movementId) =>
      PresentationError.notFoundError(s"${movementType.toString} with ID ${movementId.value} was not found")
    case PersistenceError.MessageNotFound(_, _, _) => PresentationError.internalServiceError(cause = None)
    case err: PersistenceError.Unexpected          => PresentationError.internalServiceError(cause = err.thr)
  }

  implicit val messageGenerationErrorConverter: Converter[MessageGenerationError] = {
    case MessageGenerationError.MessageTypeNotSupported(messageType) =>
      PresentationError.notImplementedError(s"Message type ${messageType.code} is not supported for this movement type")
  }

  implicit val routerErrorConverter: Converter[RouterError] = {
    case RouterError.MovementNotFound(_) => PresentationError.notFoundError(s"movement not found")
    case err: RouterError.Unexpected     => PresentationError.internalServiceError(cause = err.thr)
  }
}
