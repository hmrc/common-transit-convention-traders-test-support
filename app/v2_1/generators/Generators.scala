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

package v2_1.generators

import v2_1.utils.Strings

import java.time.Clock
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object Generators extends Generators

trait Generators {

  val clock = Clock.systemUTC()

  def generateLocalDateTime() = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(clock.instant().truncatedTo(ChronoUnit.SECONDS).atZone(ZoneOffset.UTC))

  def generateLocalDate() = DateTimeFormatter.ISO_LOCAL_DATE.format(clock.instant().atZone(ZoneOffset.UTC))

  def generateDeclarationGoodsNumber(): String = Strings.between1And9.toString ++ Strings.numeric(2)

}
