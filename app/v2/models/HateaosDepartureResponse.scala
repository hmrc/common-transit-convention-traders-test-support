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

import play.api.libs.json.JsObject
import play.api.libs.json.Json
import utils.CallOps.CallOps
import utils.Utils

import scala.xml.NodeSeq

object HateaosDepartureResponse {

  def apply(departureId: DepartureId, messageType: MessageType, body: XMLMessage, locationValue: String): JsObject = {
    val strDeparture  = departureId.value
    val messagesRoute = routing.routes.DeparturesRouter.injectEISResponse(strDeparture).urlWithContext
    val messageId     = Utils.lastFragment(locationValue)

    Json.obj(
      "_links" -> Json.obj(
        "self"      -> Json.obj("href" -> s"$messagesRoute/$messageId"),
        "departure" -> Json.obj("href" -> s"${Utils.dropLastFragment(messagesRoute)}")
      ),
      "departureId" -> strDeparture,
      "messageId"   -> messageId,
      "messageType" -> messageType.code,
      "body"        -> body.value.mkString
    )
  }
}
