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
import play.api.http.HeaderNames.LOCATION
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.http.UpstreamErrorResponse
import utils.Utils
import v2.connectors.InboundRouterConnector
import v2.models.DepartureId
import v2.models.EORINumber
import v2.models.MessageId
import v2.models.MessageType
import v2.models.errors.PersistenceError

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.control.NonFatal

@ImplementedBy(classOf[InboundRouterServiceImpl])
trait InboundRouterService {

  def post(eori: EORINumber, messageType: MessageType, message: String, departureId: DepartureId)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext): EitherT[Future, PersistenceError, MessageId]
}

@Singleton
class InboundRouterServiceImpl @Inject()(inboundRouterConnector: InboundRouterConnector) extends InboundRouterService with HttpErrorFunctions {

  def post(eori: EORINumber, messageType: MessageType, message: String, departureId: DepartureId)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext): EitherT[Future, PersistenceError, MessageId] =
    EitherT(
      inboundRouterConnector
        .post(eori, messageType, message, departureId)
        .map(response => {
          if (is2xx(response.status)) {
            response.header(LOCATION) match {
              case Some(loc) => Right(MessageId(Utils.lastFragment(loc)))
              case _         => Left(PersistenceError.UnexpectedError(None))
            }
          } else {
            Left(PersistenceError.UnexpectedError(None))
          }
        })
        .recover {
          case UpstreamErrorResponse(_, NOT_FOUND, _, _) => Left(PersistenceError.DepartureNotFound(departureId))
          case NonFatal(thr)                             => Left(PersistenceError.UnexpectedError(Some(thr)))
        }
    )
}
