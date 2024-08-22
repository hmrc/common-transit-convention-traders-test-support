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

package models

import java.time.LocalDateTime
import play.api.libs.json.Json
import play.api.libs.json.OFormat

object ResponseDeparture {

  implicit val format: OFormat[ResponseDeparture] = Json.format[ResponseDeparture]

  def apply(d: Departure): ResponseDeparture =
    ResponseDeparture(
      s"/movements/departures/${d.departureId.toString}",
      d.created,
      d.updated,
      d.movementReferenceNumber,
      d.referenceNumber,
      d.status,
      s"/movements/departures/${d.departureId.toString}/messages"
    )
}

case class ResponseDeparture(
  departure: String,
  created: LocalDateTime,
  updated: LocalDateTime,
  movementReferenceNumber: Option[String],
  referenceNumber: String,
  status: String,
  messages: String
)
