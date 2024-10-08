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

package v2_1.models

case class CorrelationId(movementId: MovementId, messageId: MessageId) {

  lazy val toFormattedString = {
    val movementId1 = movementId.value.slice(0, 8)
    val movementId2 = movementId.value.slice(8, 12)
    val movementId3 = movementId.value.slice(12, 16)

    val messageId1 = messageId.value.slice(0, 4)
    val messageId2 = messageId.value.slice(4, 16)

    s"$movementId1-$movementId2-$movementId3-$messageId1-$messageId2"
  }
}
