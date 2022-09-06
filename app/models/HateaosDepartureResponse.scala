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

package models

import play.api.libs.json.JsObject
import play.api.libs.json.Json
import utils.CallOps._
import utils.Utils

import scala.xml.NodeSeq

object HateaosDepartureResponse {

  def apply(departureId: DepartureId, messageType: MessageType, body: NodeSeq, locationValue: String): JsObject =
    apply(departureId.index.toString, messageType.code, body, locationValue)

  def apply(departureId: String, messageTypeCode: String, body: NodeSeq, locationValue: String): JsObject = {
    val messagesRoute = routing.routes.DeparturesRouter.injectEISResponse(departureId).urlWithContext
    val messageId     = Utils.lastFragment(locationValue)

    Json.obj(
      "_links" -> Json.obj(
        "self"      -> Json.obj("href" -> s"$messagesRoute/$messageId"),
        "departure" -> Json.obj("href" -> s"${Utils.dropLastFragment(messagesRoute)}")
      ),
      "departureId" -> departureId,
      "messageId"   -> messageId,
      "messageType" -> messageTypeCode,
      "body"        -> body.toString()
    )
  }
}
