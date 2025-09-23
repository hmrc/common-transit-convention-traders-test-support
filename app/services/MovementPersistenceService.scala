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

import cats.data.EitherT
import com.google.inject.Inject
import com.google.inject.Singleton
import connectors.MovementConnector
import models.*
import models.errors.PersistenceError
import play.api.http.Status.NOT_FOUND
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.UpstreamErrorResponse

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.control.NonFatal

@Singleton
class MovementPersistenceService @Inject() (movementConnector: MovementConnector) {

  def getMovement(movementType: MovementType, eori: EORINumber, movementId: MovementId)(implicit
    rh: RequestHeader,
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): EitherT[Future, PersistenceError, Movement] =
    EitherT {
      movementConnector
        .getMovement(movementType, eori, movementId)
        .map(Right(_))
        .recover {
          case UpstreamErrorResponse(_, NOT_FOUND, _, _) => Left(PersistenceError.MovementNotFound(movementType, movementId))
          case NonFatal(thr)                             => Left(PersistenceError.Unexpected(Some(thr)))
        }
    }

  def getMessage(movementType: MovementType, eori: EORINumber, movementId: MovementId, messageId: MessageId)(implicit
    request: RequestHeader,
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): EitherT[Future, PersistenceError, Message] =
    EitherT {
      movementConnector
        .getMessage(movementType, eori, movementId, messageId)
        .map(Right(_))
        .recover {
          case UpstreamErrorResponse(_, NOT_FOUND, _, _) => Left(PersistenceError.MessageNotFound(movementType, movementId, messageId))
          case NonFatal(thr)                             => Left(PersistenceError.Unexpected(Some(thr)))
        }
    }

}
