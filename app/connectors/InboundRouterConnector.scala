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

package connectors

import config.AppConfig
import models.CorrelationId
import models.MessageType
import models.WrappedXMLMessage
import play.api.libs.ws.DefaultBodyWritables
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.Authorization
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.http.client.HttpClientV2

import java.net.URL
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class InboundRouterConnector @Inject() (http: HttpClientV2, appConfig: AppConfig) extends BaseConnector with DefaultBodyWritables {

  lazy val auth: Option[Authorization] =
    if (appConfig.bearerTokenEnabled)
      Some(Authorization(s"Bearer ${appConfig.bearerTokenToken}"))
    else None

  def post(messageType: MessageType, message: WrappedXMLMessage, correlationId: CorrelationId)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[HttpResponse] =
    http
      .post(url(correlationId))(headers(messageType))
      .withBody(message.value.mkString)
      .execute[HttpResponse]

  def headers(messageType: MessageType)(implicit hc: HeaderCarrier): HeaderCarrier =
    hc
      .copy(authorization = auth)
      .withExtraHeaders(Seq("X-Message-Type" -> messageType.code)*)

  def url(correlationId: CorrelationId): URL =
    url"${appConfig.transitMovementsRouterUrl}/transit-movements-router/movements/${correlationId.toFormattedString}/messages/"

}
