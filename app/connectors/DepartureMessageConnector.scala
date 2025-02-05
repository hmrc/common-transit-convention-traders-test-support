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
import connectors.util.CustomHttpReader
import models.ChannelType
import models.domain.MovementMessage
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.http.client.HttpClientV2
import utils.Utils

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DepartureMessageConnector @Inject() (http: HttpClientV2, appConfig: AppConfig) extends BaseConnector {

  def get(departureId: String, messageId: String, channelType: ChannelType)(implicit
    request: RequestHeader,
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Either[HttpResponse, MovementMessage]] = {
    val destination = s"${appConfig.traderAtDeparturesUrl}$departureRoute${Utils.urlEncode(departureId)}/messages/${Utils.urlEncode(messageId)}"

    http
      .get(url"$destination")(enforceAuthHeaderCarrier(responseHeaders(channelType)))
      .execute[HttpResponse](using CustomHttpReader, implicitly)
      .map(extractIfSuccessful[MovementMessage])
  }
}
