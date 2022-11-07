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

package v2.models

import models.Enumerable
import models.IeMetadata

abstract class MessageType(val code: String, val rootNode: String)

object MessageType extends Enumerable.Implicits {
  case object PositiveAcknowledgement extends MessageType("IE928", "CC928C")
  case object MRNAllocated            extends MessageType("IE028", "CC028C")

  val arrivalMessages: Seq[MessageType] = Seq()

  val departureMessages: Seq[MessageType] = Seq(
    PositiveAcknowledgement,
    MRNAllocated
  )

  val values: Seq[MessageType] = arrivalMessages ++ departureMessages

  implicit val enumerable: Enumerable[MessageType] =
    Enumerable(
      values.map(
        v => v.code -> v
      ): _*
    )
}
