/*
 * Copyright 2020 HM Revenue & Customs
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
import javax.inject.Inject
import models.DepartureId
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DepartureConnector @Inject()(http: HttpClient, appConfig: AppConfig) extends BaseConnector {

  def post(messageType: String, message: String, departureId: DepartureId, messageCorrelationId: Int)(implicit requestHeader: RequestHeader,
                                                                                                      hc: HeaderCarrier,
                                                                                                      ec: ExecutionContext): Future[HttpResponse] = {
    val eisPath = departureRoute format (departureId.index, messageCorrelationId)

    val url = appConfig.traderAtDeparturesUrl + eisPath

    val newHeaders = hc
      .copy()
      .withExtraHeaders(Seq("X-Message-Type" -> messageType): _*)

    http.POSTString(url, message, requestHeaders)(CustomHttpReader, newHeaders, ec)
  }
}
