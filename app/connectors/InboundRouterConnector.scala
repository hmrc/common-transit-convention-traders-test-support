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
import config.Constants
import connectors.util.CustomHttpReader
import models.MessageType
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpResponse

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class InboundRouterConnector @Inject()(http: HttpClient, appConfig: AppConfig) extends BaseConnector {

  def post(messageType: MessageType, message: String, itemId: Int)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val xMessageRecipient = mdtpString.format(messageType.source, itemId, Constants.MessageCorrelationId)

    val newHeaders = hc
      .copy()
      .withExtraHeaders(Seq("X-Message-Recipient" -> xMessageRecipient, "X-Message-Type" -> messageType.code): _*)

    val url = appConfig.transitMovementsTraderRouterUrl + routerRoute

    http.POSTString(url, message, requestHeaders)(CustomHttpReader, newHeaders, ec)
  }
}
