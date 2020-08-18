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

import play.api.http.HeaderNames
import play.api.http.MimeTypes
import uk.gov.hmrc.http.HttpErrorFunctions

class BaseConnector extends HttpErrorFunctions {
  protected val requestHeaders: Seq[(String, String)] =
    Seq((HeaderNames.CONTENT_TYPE, MimeTypes.XML))

  protected val responseHeaders: Seq[(String, String)] =
    Seq((HeaderNames.CONTENT_TYPE, MimeTypes.JSON))

  protected val arrivalRoute   = "/transit-movements-trader-at-destination/movements/arrivals/MDTP-%d-%d/messages/eis"
  protected val departureRoute = "/transits-movements-trader-at-departure/movements/departures/MDTP-%d-%d/messages/eis"
}
