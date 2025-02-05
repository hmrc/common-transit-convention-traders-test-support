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
import models.DepartureId
import models.DepartureWithMessages
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.xml.NodeSeq

class DepartureConnector @Inject() (http: HttpClientV2, appConfig: AppConfig) extends BaseConnector {

  def getMessages(departureId: DepartureId, channelType: ChannelType)(implicit
    requestHeader: RequestHeader,
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Either[HttpResponse, DepartureWithMessages]] = {
    val url = s"${appConfig.traderAtDeparturesUrl}$departureRoute${departureId.index.toString}/messages"

    http
      .get(url"$url")(enforceAuthHeaderCarrier(responseHeaders(channelType)))
      .setHeader(responseHeaders(channelType): _*)
      .execute[HttpResponse](CustomHttpReader, ec)
      .map {
        response =>
          extractIfSuccessful[DepartureWithMessages](response)
      }

  }

  def createDeclarationMessage(requestData: NodeSeq, channelType: ChannelType)(implicit
    requestHeader: RequestHeader,
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[HttpResponse] = {
    val url                             = s"${appConfig.traderAtDeparturesUrl}$departureRoute"
    val channelHeader: (String, String) = ("Channel", channelType.toString)
    val headers: Seq[(String, String)]  = requestHeaders :+ channelHeader

    http
      .post(url"$url")(enforceAuthHeaderCarrier(Seq.empty))
      .withBody(requestData.toString())
      .setHeader(headers: _*)
      .execute[HttpResponse](CustomHttpReader, ec)

  }

}
