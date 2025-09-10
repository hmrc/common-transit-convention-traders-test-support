/*
 * Copyright 2025 HM Revenue & Customs
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

package models

enum VersionHeader(val value: String) {
  case v2_1 extends VersionHeader("application/vnd.hmrc.2.1+json")
  case v3_0 extends VersionHeader("application/vnd.hmrc.3.0+json")
}

object VersionHeader {
  def fromString(value: String): Option[VersionHeader] =
    values.find(_.value == value)
}
