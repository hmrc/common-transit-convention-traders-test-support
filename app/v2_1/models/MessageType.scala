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

import play.api.libs.json.*

abstract class MessageType(val code: String, val rootNode: String) extends Product with Serializable

object MessageType {
  case object AmendmentAcceptance               extends MessageType("IE004", "CC004C")
  case object InvalidationDecision              extends MessageType("IE009", "CC009C")
  case object PositiveAcknowledgement           extends MessageType("IE928", "CC928C")
  case object MRNAllocated                      extends MessageType("IE028", "CC028C")
  case object GoodsReleaseNotification          extends MessageType("IE025", "CC025C")
  case object ReleaseForTransit                 extends MessageType("IE029", "CC029C")
  case object UnloadingPermission               extends MessageType("IE043", "CC043C")
  case object RejectionFromOfficeOfDestination  extends MessageType("IE057", "CC057C")
  case object RejectionFromOfficeOfDeparture    extends MessageType("IE056", "CC056C")
  case object ControlDecisionNotification       extends MessageType("IE060", "CC060C")
  case object RecoveryNotification              extends MessageType("IE035", "CC035C")
  case object NoReleaseForTransit               extends MessageType("IE051", "CC051C")
  case object GuaranteeNotValid                 extends MessageType("IE055", "CC055C")
  case object Discrepancies                     extends MessageType("IE019", "CC019C")
  case object WriteOffNotification              extends MessageType("IE045", "CC045C")
  case object ForwardedIncidentNotificationToED extends MessageType("IE182", "CC182C")

  val arrivalMessages: Seq[MessageType] = Seq(
    GoodsReleaseNotification,
    UnloadingPermission,
    RejectionFromOfficeOfDestination
  )

  val departureMessages: Seq[MessageType] = Seq(
    AmendmentAcceptance,
    InvalidationDecision,
    PositiveAcknowledgement,
    MRNAllocated,
    ReleaseForTransit,
    RejectionFromOfficeOfDeparture,
    ControlDecisionNotification,
    RecoveryNotification,
    NoReleaseForTransit,
    GuaranteeNotValid,
    Discrepancies,
    WriteOffNotification,
    ForwardedIncidentNotificationToED
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
