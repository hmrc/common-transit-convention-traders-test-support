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

package generators

import utils.Format

import java.time.Clock
import java.time.LocalDateTime

class Generator(clock: Clock) {

  private def now: LocalDateTime = LocalDateTime.now(clock)

  def localDateTime: String = now.format(Format.dateTimeFormatter)

  def localDate: String = localDateAdjusted(0)

  def localDateAdjusted(daysToAdd: Int): String =
    now.toLocalDate
      .plusDays(daysToAdd)
      .format(Format.dateFormatter)

  def localTime: String =
    now.toLocalTime
      .format(Format.timeFormatter)

  val isoDateTimeLength = 19 // don't include the decimal part of seconds
  def isoDateTime       = now.format(Format.isoDateTimeFormatter).substring(0, isoDateTimeLength)

  def isoDate: String = now.format(Format.isoDateFormatter)

}
