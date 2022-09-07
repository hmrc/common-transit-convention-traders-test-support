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

package v2.connectors

import config.AppConfig
import config.Constants
import connectors.util.CustomHttpReader
import v2.models.DepartureId
import v2.models.EORINumber
import v2.models.MessageType
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpResponse

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class InboundRouterConnector @Inject()(http: HttpClient, appConfig: AppConfig) extends BaseConnector {

  // Create a new message with the transit-movements-router service
  def post(eori: EORINumber, messageType: MessageType, message: String, departureId: DepartureId)(implicit hc: HeaderCarrier,
                                                                                                  ec: ExecutionContext): Future[HttpResponse] = {
    val xMessageRecipient = mdtpString format (messageType.source, departureId.value, Constants.MessageCorrelationId)

    val newHeaders = hc
      .copy()
      .withExtraHeaders(Seq("X-Message-Recipient" -> xMessageRecipient, "X-Message-Type" -> messageType.code): _*)

    val url = appConfig.transitMovementsRouterUrl + s"/traders/${eori.value}/movements/$messageType/$departureId/messages/"

    http.POSTString(url, message, requestHeaders)(CustomHttpReader, newHeaders, ec)
  }
}
