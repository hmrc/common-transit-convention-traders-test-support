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

import cats.data.EitherT
import com.google.inject.ImplementedBy
import com.google.inject.Inject
import com.google.inject.Singleton
import play.api.http.Status.NOT_FOUND
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.UpstreamErrorResponse
import v2.connectors.DepartureConnector
import v2.models.errors.PersistenceError
import v2.models.DepartureId
import v2.models.DepartureWithoutMessages
import v2.models.EORINumber
import v2.models.Message
import v2.models.MessageId

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.control.NonFatal

@ImplementedBy(classOf[DepartureServiceImpl])
trait DepartureService {

  def getDeparture(eori: EORINumber, departureId: DepartureId)(implicit rh: RequestHeader,
                                                               hc: HeaderCarrier,
                                                               ec: ExecutionContext): EitherT[Future, PersistenceError, DepartureWithoutMessages]

  def getMessage(eori: EORINumber, departureId: DepartureId, messageId: MessageId)(implicit
                                                                                   request: RequestHeader,
                                                                                   hc: HeaderCarrier,
                                                                                   ec: ExecutionContext): EitherT[Future, PersistenceError, Message]
}

@Singleton
class DepartureServiceImpl @Inject()(departureConnector: DepartureConnector) extends DepartureService {

  override def getDeparture(eori: EORINumber, departureId: DepartureId)(implicit
                                                                        rh: RequestHeader,
                                                                        hc: HeaderCarrier,
                                                                        ec: ExecutionContext): EitherT[Future, PersistenceError, DepartureWithoutMessages] =
    EitherT {
      departureConnector
        .getDeparture(eori, departureId)
        .map(Right(_))
        .recover {
          case UpstreamErrorResponse(_, NOT_FOUND, _, _) => Left(PersistenceError.DepartureNotFound(departureId))
          case NonFatal(thr)                             => Left(PersistenceError.UnexpectedError(Some(thr)))
        }
    }

  override def getMessage(eori: EORINumber, departureId: DepartureId, messageId: MessageId)(implicit
                                                                                            request: RequestHeader,
                                                                                            hc: HeaderCarrier,
                                                                                            ec: ExecutionContext): EitherT[Future, PersistenceError, Message] =
    EitherT {
      departureConnector
        .getMessage(eori, departureId, messageId)
        .map(Right(_))
        .recover {
          case UpstreamErrorResponse(_, NOT_FOUND, _, _) => Left(PersistenceError.DepartureNotFound(departureId))
          case NonFatal(thr)                             => Left(PersistenceError.UnexpectedError(Some(thr)))
        }
    }

}
