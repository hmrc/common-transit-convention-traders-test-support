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
import play.api.http.Status.OK
import play.api.libs.ws.DefaultBodyWritables
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.Authorization
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.http.client.HttpClientV2
import v2_1.models.*
import v2_1.models.formats.CommonFormats.messageFormat
import v2_1.models.formats.CommonFormats.movementFormat
import v2_1.utils.Utils

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class MovementConnector @Inject() (http: HttpClientV2, appConfig: AppConfig) extends BaseConnector with DefaultBodyWritables {

  def getMovement(movementType: MovementType, eori: EORINumber, movementId: MovementId)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Movement] = {
    val url                  = constructMovementUri(movementType, eori, movementId)
    val updatedHeaderCarrier = hc.copy(authorization = Some(Authorization(appConfig.internalAuthToken)))

    http
      .get(url)(updatedHeaderCarrier)
      .execute[HttpResponse]
      .flatMap {
        response =>
          response.status match {
            case OK => response.as[Movement]
            case _  => response.error
          }
      }
  }

  def getMessage(movementType: MovementType, eori: EORINumber, movementId: MovementId, messageId: MessageId)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Message] = {

    val uri                  = constructMessageUri(movementType, eori, movementId, messageId)
    val updatedHeaderCarrier = hc.copy(authorization = Some(Authorization(appConfig.internalAuthToken))).copy(otherHeaders = responseHeaders)

    http
      .get(uri)(updatedHeaderCarrier)
      .execute[HttpResponse]
      .flatMap {
        response =>
          response.status match {
            case OK => response.as[Message]
            case _  => response.error
          }
      }
  }

  private def constructBaseUri(movementType: MovementType, eori: EORINumber) =
    url"${appConfig.transitMovementsUrl}/transit-movements/traders/${eori.value}/movements/${movementType.urlFragment}"

  private def constructMovementUri(movementType: MovementType, eori: EORINumber, movementId: MovementId) =
    url"${constructBaseUri(movementType, eori)}/${Utils.urlEncode(movementId.value)}"

  private def constructMessageUri(movementType: MovementType, eori: EORINumber, movementId: MovementId, messageId: MessageId) =
    url"${constructMovementUri(movementType, eori, movementId)}/messages/${Utils.urlEncode(messageId.value)}"

}
