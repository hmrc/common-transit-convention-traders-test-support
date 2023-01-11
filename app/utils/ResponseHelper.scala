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

package utils

import play.api.http.Status
import play.api.mvc.Result
import play.api.mvc.Results
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.http.HttpResponse

trait ResponseHelper extends Results with Status with HttpErrorFunctions {

  def handleNon2xx(response: HttpResponse): Result =
    response.status match {
      case s if is4xx(s) => if (response.body != null) Status(response.status)(response.body) else Status(response.status)
      case _             => Status(response.status)
    }
}
