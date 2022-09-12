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

package v2.services

import com.google.inject.Inject
import v2.generators.DepartureMessageGenerator
import v2.models.DepartureId
import v2.models.MessageType
import v2.models.request.MessageRequest
import play.api.libs.json.JsValue

import scala.xml.NodeSeq

class MessageGenerationService @Inject()(
  departureMessageGenerator: DepartureMessageGenerator
) {

  def generateMessage(request: MessageRequest[JsValue], departureId: DepartureId): NodeSeq = {
    val pf = departureMessageGenerator.generate(departureId) orElse default()
    pf.apply(request.messageType)
  }

  private def default(): PartialFunction[MessageType, NodeSeq] = {
    case _ => throw new Exception("Unsupported Message Type")
  }

}
