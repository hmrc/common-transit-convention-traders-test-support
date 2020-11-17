/*
 * Copyright 2020 HM Revenue & Customs
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

sealed trait XSDFile {
  val FilePath: String
  val Label: String
}

sealed trait ArrivalMessage
sealed trait DepartureMessage

object XSDFile {

  val Definitions = Seq(
    ArrivalRejectionXSD,
    GoodsReleasedXSD,
    UnloadingPermissionXSD,
    UnloadingRemarksRejectionXSD,
    PositiveAcknowledgementXSD,
    NoReleaseForTransitXSD,
    ReleaseForTransitXSD,
    ControlDecisionNotificationXSD,
    MrnAllocatedXSD,
    DeclarationRejectedXSD,
    CancellationDecisionXSD,
    WriteOffNotificationXSD,
    GuaranteeNotValidXSD
  )

  val DefinitionsMap = Definitions.map(xsd => xsd.Label -> xsd).toMap

  val Arrivals: Map[String, XSDFile] = DefinitionsMap.filter {
    case (key: String, value: XSDFile) =>
      value.isInstanceOf[ArrivalMessage]
  }

  val Departures: Map[String, XSDFile] = DefinitionsMap.filter {
    case (key: String, value: XSDFile) =>
      value.isInstanceOf[DepartureMessage]
  }

  // Internal
  object SimpleXSD extends XSDFile {
    val FilePath: String = "/xsd/simple.xsd"
    val Label: String    = "messages"
  }

  // Arrivals
  object ArrivalRejectionXSD extends XSDFile with ArrivalMessage {
    val FilePath = s"/xsd/${MessageType.ArrivalRejection.rootNode}.xsd"
    val Label    = s"${MessageType.ArrivalRejection.rootNode}"
  }

  object GoodsReleasedXSD extends XSDFile with ArrivalMessage {
    val FilePath = s"/xsd/${MessageType.GoodsReleased.rootNode}.xsd"
    val Label    = s"${MessageType.GoodsReleased.rootNode}"
  }

  object UnloadingPermissionXSD extends XSDFile with ArrivalMessage {
    val FilePath = s"/xsd/${MessageType.UnloadingPermission.rootNode}.xsd"
    val Label    = s"${MessageType.UnloadingPermission.rootNode}"
  }

  object UnloadingRemarksRejectionXSD extends XSDFile with ArrivalMessage {
    val FilePath = s"/xsd/${MessageType.UnloadingRemarksRejection.rootNode}.xsd"
    val Label    = s"${MessageType.UnloadingRemarksRejection.rootNode}"
  }

  // Departures
  object PositiveAcknowledgementXSD extends XSDFile with DepartureMessage {
    val FilePath = s"/xsd/${MessageType.PositiveAcknowledgement.rootNode}.xsd"
    val Label    = s"${MessageType.PositiveAcknowledgement.rootNode}"
  }

  object NoReleaseForTransitXSD extends XSDFile with DepartureMessage {
    val FilePath = s"/xsd/${MessageType.NoReleaseForTransit.rootNode}.xsd"
    val Label    = s"${MessageType.NoReleaseForTransit.rootNode}"
  }

  object ReleaseForTransitXSD extends XSDFile with DepartureMessage {
    val FilePath = s"/xsd/${MessageType.ReleaseForTransit.rootNode}.xsd"
    val Label    = s"${MessageType.ReleaseForTransit.rootNode}"
  }

  object ControlDecisionNotificationXSD extends XSDFile with DepartureMessage {
    val FilePath = s"/xsd/${MessageType.ControlDecisionNotification.rootNode}.xsd"
    val Label    = s"${MessageType.ControlDecisionNotification.rootNode}"
  }

  object MrnAllocatedXSD extends XSDFile with DepartureMessage {
    val FilePath = s"/xsd/${MessageType.MrnAllocated.rootNode}.xsd"
    val Label    = s"${MessageType.MrnAllocated.rootNode}"
  }

  object DeclarationRejectedXSD extends XSDFile with DepartureMessage {
    val FilePath = s"/xsd/${MessageType.DeclarationRejected.rootNode}.xsd"
    val Label    = s"${MessageType.DeclarationRejected.rootNode}"
  }

  object CancellationDecisionXSD extends XSDFile with DepartureMessage {
    val FilePath = s"/xsd/${MessageType.CancellationDecision.rootNode}.xsd"
    val Label    = s"${MessageType.CancellationDecision.rootNode}"
  }

  object WriteOffNotificationXSD extends XSDFile with DepartureMessage {
    val FilePath = s"/xsd/${MessageType.WriteOffNotification.rootNode}.xsd"
    val Label    = s"${MessageType.WriteOffNotification.rootNode}"
  }

  object GuaranteeNotValidXSD extends XSDFile with DepartureMessage {
    val FilePath = s"/xsd/${MessageType.GuaranteeNotValid.rootNode}.xsd"
    val Label    = s"${MessageType.GuaranteeNotValid.rootNode}"
  }
}
