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
import config.Constants
import connectors.util.CustomHttpReader
import javax.inject.Inject
import models.ArrivalId
import models.DepartureId
import models.ItemId
import play.api.http.HeaderNames
import play.api.http.MimeTypes
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.logging.Authorization
import utils.Utils

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class BaseConnector @Inject()(http: HttpClient, appConfig: AppConfig) extends HttpErrorFunctions {
  protected val requestHeaders: Seq[(String, String)] =
    Seq((HeaderNames.CONTENT_TYPE, MimeTypes.XML))

  protected val responseHeaders: Seq[(String, String)] =
    Seq((HeaderNames.CONTENT_TYPE, MimeTypes.JSON))

  protected val arrivalRoute   = "/transit-movements-trader-at-destination/movements/arrivals/MDTP-%d-%d/messages/eis"
  protected val departureRoute = "/transits-movements-trader-at-departure/movements/departures/MDTP-%d-%d/messages/eis"

  protected val arrivalGetRoute   = "/transit-movements-trader-at-destination/movements/arrivals/"
  protected val departureGetRoute = "/transits-movements-trader-at-departure/movements/departures/"

  protected def enforceAuthHeaderCarrier(extraHeaders: Seq[(String, String)])(implicit requestHeader: RequestHeader,
                                                                              headerCarrier: HeaderCarrier): HeaderCarrier = {
    val newHeaderCarrier = headerCarrier
      .copy(authorization = Some(Authorization(requestHeader.headers.get(HeaderNames.AUTHORIZATION).getOrElse(""))))
      .withExtraHeaders(extraHeaders: _*)
    newHeaderCarrier
  }

  def post(messageType: String, message: String, itemId: ItemId)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url = itemId match {
      case ArrivalId(index) =>
        appConfig.traderAtDestinationUrl + arrivalRoute format (index, Constants.MessageCorrelationId)
      case DepartureId(index) =>
        appConfig.traderAtDeparturesUrl + departureRoute format (index, Constants.MessageCorrelationId)
    }

    val newHeaders = hc
      .copy()
      .withExtraHeaders(Seq("X-Message-Type" -> messageType): _*)

    http.POSTString(url, message, requestHeaders)(CustomHttpReader, newHeaders, ec)
  }

  def get(itemId: ItemId)(implicit requestHeader: RequestHeader, hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url = itemId match {
      case ArrivalId(index)   => appConfig.traderAtDeparturesUrl + arrivalGetRoute + Utils.urlEncode(index.toString)
      case DepartureId(index) => appConfig.traderAtDeparturesUrl + departureGetRoute + Utils.urlEncode(index.toString)
    }

    http.GET[HttpResponse](url, queryParams = Seq(), responseHeaders)(CustomHttpReader, enforceAuthHeaderCarrier(responseHeaders), ec)
  }
}
