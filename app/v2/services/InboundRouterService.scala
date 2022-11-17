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
import config.Constants.MessageIdHeaderKey
import play.api.http.Status.NOT_FOUND
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.http.UpstreamErrorResponse
import utils.Utils
import v2.connectors.InboundRouterConnector
import v2.models.MovementId
import v2.models.MessageId
import v2.models.MessageType
import v2.models.XMLMessage
import v2.models.errors.RouterError

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.control.NonFatal

@ImplementedBy(classOf[InboundRouterServiceImpl])
trait InboundRouterService {

  def post(messageType: MessageType, message: XMLMessage, departureId: MovementId)(implicit
                                                                                   hc: HeaderCarrier,
                                                                                   ec: ExecutionContext): EitherT[Future, RouterError, MessageId]
}

@Singleton
class InboundRouterServiceImpl @Inject()(inboundRouterConnector: InboundRouterConnector) extends InboundRouterService with HttpErrorFunctions {

  def post(messageType: MessageType, message: XMLMessage, movementId: MovementId)(implicit
                                                                                  hc: HeaderCarrier,
                                                                                  ec: ExecutionContext): EitherT[Future, RouterError, MessageId] =
    EitherT(
      inboundRouterConnector
        .post(messageType, message.wrapped, movementId)
        .map {
          response =>
            if (is2xx(response.status)) {
              response.header(MessageIdHeaderKey) match {
                case Some(value) => Right(MessageId(Utils.lastFragment(value)))
                case _           => Left(RouterError.Unexpected(Some(new Exception("X-Message-Id header missing from router response"))))
              }
            } else {
              Left(RouterError.Unexpected(None))
            }
        }
        .recover {
          case UpstreamErrorResponse(_, NOT_FOUND, _, _) => Left(RouterError.MovementNotFound(movementId))
          case NonFatal(thr)                             => Left(RouterError.Unexpected(Some(thr)))
        }
    )

}
