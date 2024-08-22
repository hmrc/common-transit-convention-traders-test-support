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

import config.Constants
import v2_1.models.MessageType
import v2_1.models.MovementId
import v2_1.models.XMLMessage

trait MessageGenerator {

  def correlationId(movementId: MovementId) = s"${movementId.value}-${Constants.DefaultTriggerId}"

  final def generate(movementId: MovementId): PartialFunction[MessageType, XMLMessage] =
    generateWithCorrelationId(correlationId(movementId))

  protected def generateWithCorrelationId(correlationId: String): PartialFunction[MessageType, XMLMessage]

}
