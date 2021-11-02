/*
 * Copyright 2021 HM Revenue & Customs
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

package services

import com.google.inject.Inject
import generators.UnloadingPermissionGenerator
import models.MessageType.ArrivalNegativeAcknowledgement
import models.MessageType.ArrivalRejection
import models.MessageType.CancellationDecision
import models.MessageType.ControlDecisionNotification
import models.MessageType.DeclarationRejected
import models.MessageType.GoodsReleased
import models.MessageType.GuaranteeNotValid
import models.MessageType.MrnAllocated
import models.MessageType.NoReleaseForTransit
import models.MessageType.PositiveAcknowledgement
import models.MessageType.ReleaseForTransit
import models.MessageType.UnloadingPermission
import models.MessageType.UnloadingRemarksRejection
import models.MessageType.WriteOffNotification
import models.MessageType.XMLSubmissionNegativeAcknowledgement
import models.generation.UnloadingPermissionGenInstructions
import models.request.MessageRequest
import play.api.libs.json.JsValue
import utils.Messages

import scala.xml.NodeSeq

class MessageGenerationService @Inject()(unloadingPermissionGenerator: UnloadingPermissionGenerator) {

  def generateMessage(request: MessageRequest[JsValue]): NodeSeq = request.messageType match {

    case PositiveAcknowledgement              => Messages.Departure.generateIE928Message()
    case NoReleaseForTransit                  => Messages.Departure.generateIE051Message()
    case ReleaseForTransit                    => Messages.Departure.generateIE029Message()
    case ControlDecisionNotification          => Messages.Departure.generateIE060Message()
    case MrnAllocated                         => Messages.Departure.generateIE028Message()
    case DeclarationRejected                  => Messages.Departure.generateIE016Message()
    case CancellationDecision                 => Messages.Departure.generateIE009Message()
    case WriteOffNotification                 => Messages.Departure.generateIE045Message()
    case GuaranteeNotValid                    => Messages.Departure.generateIE055Message()
    case XMLSubmissionNegativeAcknowledgement => Messages.Departure.generateIE917Message()
    case ArrivalNegativeAcknowledgement       => Messages.Departure.generateIE917Message()

    case ArrivalRejection          => Messages.Arrival.generateIE008Message()
    case UnloadingPermission       => unloadingPermissionGenerator.generate(request.instructions.asInstanceOf[UnloadingPermissionGenInstructions])
    case UnloadingRemarksRejection => Messages.Arrival.generateIE058Message()
    case GoodsReleased             => Messages.Arrival.generateIE025Message()

    case _ => throw new Exception("Unsupported Message Type")
  }

}
