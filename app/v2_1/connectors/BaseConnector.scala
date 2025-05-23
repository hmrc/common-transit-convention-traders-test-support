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

import play.api.http.HeaderNames
import play.api.http.MimeTypes
import play.api.libs.json.JsResult
import play.api.libs.json.Reads
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.UpstreamErrorResponse

import scala.concurrent.Future

class BaseConnector extends HttpErrorFunctions {

  lazy val responseHeaders: Seq[(String, String)] =
    Seq((HeaderNames.CONTENT_TYPE, MimeTypes.JSON))

  implicit class HttpResponseHelpers(response: HttpResponse) {

    def as[A](implicit reads: Reads[A]): Future[A] =
      response.json
        .validate[A]
        .map(
          result => Future.successful(result)
        )
        .recoverTotal(
          error => Future.failed(JsResult.Exception(error))
        )

    def error[A]: Future[A] =
      Future.failed(UpstreamErrorResponse(response.body, response.status))

  }
}
