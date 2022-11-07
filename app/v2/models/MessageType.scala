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

import play.api.libs.json.JsError
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.Reads
import play.api.libs.json.Writes

abstract class MessageType(val code: String, val rootNode: String) extends Product with Serializable

object MessageType {
  case object PositiveAcknowledgement  extends MessageType("IE928", "CC928C")
  case object MRNAllocated             extends MessageType("IE028", "CC028C")
  case object GoodsReleaseNotification extends MessageType("IE025", "CC025C")

  val arrivalMessages: Seq[MessageType] = Seq(
    GoodsReleaseNotification
  )

  val departureMessages: Seq[MessageType] = Seq(
    PositiveAcknowledgement,
    MRNAllocated
  )

  val values: Seq[MessageType] = arrivalMessages ++ departureMessages

  def findByCode(code: String) = values.find(_.code == code)

  implicit val reads: Reads[MessageType] = Reads {
    case JsString(value) => findByCode(value).map(JsSuccess(_)).getOrElse(JsError("Code not found"))
    case _               => JsError()
  }

  implicit val writes: Writes[MessageType] = Writes {
    messageType =>
      JsString(messageType.code)
  }
}
