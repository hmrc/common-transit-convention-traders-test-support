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

package config

object Constants {
  val MessageCorrelationId = 1

  val Context = "/customs/transits"

  val MessageIdHeaderKey: String = "X-Message-Id"

  val LegacyEnrolmentKey: String   = "HMCE-NCTS-ORG"
  val LegacyEnrolmentIdKey: String = "VATRegNoTURN"

  val NewEnrolmentKey: String   = "HMRC-CTC-ORG"
  val NewEnrolmentIdKey: String = "EORINumber"

  val DefaultTriggerId: String = List.fill(16)("0").mkString
}
