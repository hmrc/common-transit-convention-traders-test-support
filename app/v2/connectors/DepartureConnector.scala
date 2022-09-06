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
import connectors.util.CustomHttpReader
import v2.models.DepartureId
import v2.models.DepartureWithoutMessages
import v2.models.Message
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpResponse
import utils.Utils
import v2.models.formats.PresentationFormats.departureWithoutMessagesFormat
import v2.models.formats.PresentationFormats.messageFormat

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DepartureConnector @Inject()(http: HttpClient, appConfig: AppConfig) extends BaseConnector {

  def getDeparture(eori: String, departureId: DepartureId)(implicit requestHeader: RequestHeader,
                                                           hc: HeaderCarrier,
                                                           ec: ExecutionContext): Future[Either[HttpResponse, DepartureWithoutMessages]] = {
    val url = s"${appConfig.transitMovementsUrl}/traders/$eori/movements/departures/${departureId.value}"

    http
      .GET[HttpResponse](url, queryParams = Seq())(CustomHttpReader, enforceAuthHeaderCarrier(Seq()), ec)
      .map {
        response =>
          extractIfSuccessful[DepartureWithoutMessages](response)
      }
  }

  // /traders/:EORI/movements/departures/:departureId/messages/:messageId
  def getMessage(eori: String, departureId: String, messageId: String)(implicit request: RequestHeader,
                                                                       hc: HeaderCarrier,
                                                                       ec: ExecutionContext): Future[Either[HttpResponse, Message]] = {
    val url = s"${appConfig.transitMovementsUrl}/traders/$eori/movements/departures/${Utils.urlEncode(departureId)}/messages/${Utils.urlEncode(messageId)}"

    http
      .GET[HttpResponse](url, queryParams = Seq(), responseHeaders())(CustomHttpReader, enforceAuthHeaderCarrier(Seq()), ec)
      .map {
        response =>
          extractIfSuccessful[Message](response)
      }
  }

//  def createDeclarationMessage(requestData: NodeSeq, channelType: ChannelType)(implicit requestHeader: RequestHeader,
//                                                                               hc: HeaderCarrier,
//                                                                               ec: ExecutionContext): Future[HttpResponse] = {
//    val url                             = s"${appConfig.traderAtDeparturesUrl}$departureRoute"
//    val channelHeader: (String, String) = ("Channel", channelType.toString)
//    val headers: Seq[(String, String)]  = requestHeaders :+ channelHeader
//
//    http.POSTString[HttpResponse](url, requestData.toString, headers)(CustomHttpReader, enforceAuthHeaderCarrier(Seq.empty), ec)
//  }

}
