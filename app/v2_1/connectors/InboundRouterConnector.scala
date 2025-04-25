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

package v2_1.connectors

import config.AppConfig
import play.api.libs.ws.DefaultBodyWritables
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.Authorization
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.http.client.HttpClientV2
import v2_1.models.CorrelationId
import v2_1.models.MessageType
import v2_1.models.WrappedXMLMessage

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class InboundRouterConnector @Inject() (http: HttpClientV2, appConfig: AppConfig) extends BaseConnector with DefaultBodyWritables {

  lazy val auth: Option[Authorization] =
    if (appConfig.bearerTokenEnabled) Some(Authorization(s"Bearer ${appConfig.bearerTokenToken}"))
    else None

  // Create a new message with the transit-movements-router service
  def post(messageType: MessageType, message: WrappedXMLMessage, correlationId: CorrelationId)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[HttpResponse] = {
    val newHeaders = hc
      .copy(authorization = auth)
      .withExtraHeaders(Seq("X-Message-Type" -> messageType.code)*)

    val url = url"${appConfig.transitMovementsRouterUrl}/transit-movements-router/movements/${correlationId.toFormattedString}/messages/"

    http
      .post(url)(newHeaders)
      .withBody(message.value.mkString)
      .execute[HttpResponse]
  }
}
