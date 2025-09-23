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

package generators

import config.Constants
import models.MessageType
import models.MovementId
import models.XMLMessage
import utils.Strings

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.Clock
import java.time.ZoneOffset

trait MessageGenerator {

  private val clock = Clock.systemUTC()

  def generateLocalDateTime(): String = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(clock.instant().truncatedTo(ChronoUnit.SECONDS).atZone(ZoneOffset.UTC))

  def generateLocalDate(): String = DateTimeFormatter.ISO_LOCAL_DATE.format(clock.instant().atZone(ZoneOffset.UTC))

  def generateDeclarationGoodsNumber(): String = Strings.between1And9.toString ++ Strings.numeric(2)

  def correlationId(movementId: MovementId) = s"${movementId.value}-${Constants.DefaultTriggerId}"

  final def generate(movementId: MovementId): PartialFunction[MessageType, XMLMessage] =
    generateWithCorrelationId(correlationId(movementId))

  protected def generateWithCorrelationId(correlationId: String): PartialFunction[MessageType, XMLMessage]

}
