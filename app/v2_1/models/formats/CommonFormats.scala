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

package v2_1.models.formats

import cats.data.NonEmptyList
import play.api.libs.functional.syntax.toInvariantFunctorOps
import play.api.libs.json.Format
import play.api.libs.json.Json
import v2_1.models.EORINumber
import v2_1.models.Message
import v2_1.models.MessageId
import v2_1.models.Movement

object CommonFormats extends CommonFormats

trait CommonFormats {
  implicit val messageIdFormat: Format[MessageId]   = Json.valueFormat[MessageId]
  implicit val messageFormat: Format[Message]       = Json.format[Message]
  implicit val eoriNumberFormat: Format[EORINumber] = Json.valueFormat[EORINumber]
  implicit val movementFormat: Format[Movement]     = Json.format[Movement]

  implicit def nonEmptyListFormat[A: Format]: Format[NonEmptyList[A]] =
    Format
      .of[List[A]]
      .inmap(
        NonEmptyList.fromListUnsafe,
        _.toList
      )

}
