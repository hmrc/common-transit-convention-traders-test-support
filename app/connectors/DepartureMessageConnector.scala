/*
 * Copyright 2021 HM Revenue & Customs
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

import javax.inject.Inject
import models.domain.MovementMessage
import models.request.ChannelRequest
import models.request.MessageRequest
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import utils.Utils

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DepartureMessageConnector @Inject()(http: HttpClient, appConfig: AppConfig) extends BaseConnector {

  def get(departureId: String,
          messageId: String)(implicit request: MessageRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Either[HttpResponse, MovementMessage]] = {
    val url = s"${appConfig.traderAtDeparturesUrl}$departureGetRoute${Utils.urlEncode(departureId)}/messages/${Utils.urlEncode(messageId)}"

    http
      .GET[HttpResponse](url, queryParams = Seq(), responseHeaders(request.request.channel))(CustomHttpReader,
                                                                                             enforceAuthHeaderCarrier(responseHeaders(request.request.channel)),
                                                                                             ec)
      .map {
        response =>
          extractIfSuccessful[MovementMessage](response)
      }
  }
}
