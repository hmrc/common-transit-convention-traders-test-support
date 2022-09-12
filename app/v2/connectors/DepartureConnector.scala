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
import play.api.http.Status.OK
import v2.models.DepartureId
import v2.models.DepartureWithoutMessages
import v2.models.EORINumber
import v2.models.Message
import v2.models.MessageId
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpResponse
import utils.Utils
import v2.models.formats.CommonFormats.departureWithoutMessagesFormat
import v2.models.formats.CommonFormats.messageFormat

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DepartureConnector @Inject()(http: HttpClient, appConfig: AppConfig) extends BaseConnector {

  def getDeparture(eori: EORINumber, departureId: DepartureId)(implicit
                                                               requestHeader: RequestHeader,
                                                               hc: HeaderCarrier,
                                                               ec: ExecutionContext): Future[DepartureWithoutMessages] = {
    val url = constructDepartureUri(eori, departureId)

    http
      .GET[HttpResponse](url, queryParams = Seq())(CustomHttpReader, enforceAuthHeaderCarrier(Seq()), ec)
      .flatMap {
        response =>
          response.status match {
            case OK => response.as[DepartureWithoutMessages]
            case _  => response.error
          }
      }
  }

  def getMessage(eori: EORINumber, departureId: DepartureId, messageId: MessageId)(implicit
                                                                                   request: RequestHeader,
                                                                                   hc: HeaderCarrier,
                                                                                   ec: ExecutionContext): Future[Message] = {

    val uri = constructMessageUri(eori, departureId, messageId)

    http
      .GET[HttpResponse](uri, queryParams = Seq(), responseHeaders)(CustomHttpReader, enforceAuthHeaderCarrier(Seq()), ec)
      .flatMap {
        response =>
          response.status match {
            case OK => response.as[Message]
            case _  => response.error
          }
      }
  }

  private def constructBaseUri(eori: EORINumber) =
    s"${appConfig.transitMovementsUrl}/transit-movements/traders/${eori.value}/movements/departures"

  private def constructDepartureUri(eori: EORINumber, departureId: DepartureId) =
    s"${constructBaseUri(eori)}/${Utils.urlEncode(departureId.value)}"

  private def constructMessageUri(eori: EORINumber, departureId: DepartureId, messageId: MessageId) =
    s"${constructDepartureUri(eori, departureId)}/messages/${Utils.urlEncode(messageId.value)}"

}
