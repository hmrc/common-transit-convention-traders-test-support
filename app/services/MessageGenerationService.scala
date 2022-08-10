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

package services

import com.google.inject.Inject
import generators.ArrivalMessageGenerator
import generators.DepartureMessageGenerator
import generators.UnloadingPermissionGenerator
import models.MessageType
import models.request.MessageRequest
import play.api.libs.json.JsValue

import scala.xml.NodeSeq

class MessageGenerationService @Inject()(
  departureMessageGenerator: DepartureMessageGenerator,
  arrivalMessageGenerator: ArrivalMessageGenerator,
  unloadingPermissionGenerator: UnloadingPermissionGenerator
) {

  def generateMessage(request: MessageRequest[JsValue]): NodeSeq = {
    val pf = departureMessageGenerator.generate() orElse
      arrivalMessageGenerator.generate() orElse
      unloadingPermissionGenerator.generate(request.instructions) orElse {
      default()
    }

    pf.apply(request.messageType)
  }

  private def default(): PartialFunction[MessageType, NodeSeq] = {
    case _ => throw new Exception("Unsupported Message Type")
  }

}
