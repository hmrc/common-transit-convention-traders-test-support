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

package models.generation

import models.MessageType
import models.MessageType.ArrivalNegativeAcknowledgement
import models.MessageType.XMLSubmissionNegativeAcknowledgement
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads
import play.api.libs.json._

case class TestMessage(messageType: MessageType)

object TestMessage {
  implicit val readsTestMessage: Reads[TestMessage] =
    (
      (__ \ "message" \ "messageType").read[String] and
        (__ \ "message" \ "source").readNullable[String]
    ).tupled.flatMap {
      case ("IE917", Some("DEP")) => Reads.pure(TestMessage(XMLSubmissionNegativeAcknowledgement))
      case ("IE917", Some("ARR")) => Reads.pure(TestMessage(ArrivalNegativeAcknowledgement))
      case _                      => (__ \ "message" \ "messageType").read[MessageType].map(TestMessage.apply)
    }

  implicit val writes: Writes[TestMessage] = Json.writes[TestMessage]
}
