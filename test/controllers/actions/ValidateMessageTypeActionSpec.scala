/*
 * Copyright 2025 HM Revenue & Customs
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

///*
// * Copyright 2023 HM Revenue & Customs
// *
// * Licensed under the Apache License, VersionHeader 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
package controllers.actions

import models.Message
import models.MessageType
import models.MovementType
import models.VersionHeader
import models.VersionHeader.v2_1
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.HeaderNames
import play.api.http.MimeTypes
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.Results.Created
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ValidateMessageTypeActionSpec extends AnyFreeSpec with Matchers with OptionValues with ScalaFutures with MockitoSugar with BeforeAndAfterEach {

  def createExampleRequest(messageType: MessageType): JsValue = Json.parse(s"""{
       |     "message": {
       |         "messageType": "${messageType.code}"
       |     }
       | }""".stripMargin)

  def createMessage(message: Message, movementType: MovementType): Message =
    movementType match {
      case MovementType.Arrival =>
        val messageType = Gen.oneOf(MessageType.arrivalMessages).sample.value
        message.copy(messageType = messageType)
      case MovementType.Departure =>
        val messageType = Gen.oneOf(MessageType.departureMessages).sample.value
        message.copy(messageType = messageType)
    }

  private def createRequest(messageType: MessageType): ValidatedVersionedRequest[JsValue] =
    ValidatedVersionedRequest(
      v2_1,
      AuthRequest(
        FakeRequest(
          "POST",
          "/",
          FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> MimeTypes.JSON, HeaderNames.ACCEPT -> VersionHeader.v2_1.value)),
          createExampleRequest(messageType)
        ),
        "a"
      )
    )

  "ValidateMessageTypeAction - departures" - {
    val validateMessageType = new MessageRequestRefiner()(implicitly).apply(MovementType.Departure)

    for (messageType <- MessageType.departureMessages)
      s"${messageType.code}" - {
        "must execute the block when passed in a valid departure message type" in {
          val result = validateMessageType.invokeBlock(
            createRequest(messageType),
            _ => Future.successful(Created)
          )
          status(result) mustEqual CREATED
        }

        "must return NotImplemented when passed in an invalid test message" in {
          val invalidMessageType = Gen.oneOf(MessageType.arrivalMessages).sample.value

          val result = validateMessageType.invokeBlock(
            createRequest(invalidMessageType),
            _ => Future.successful(Created)
          )
          status(result) mustEqual NOT_IMPLEMENTED
        }
      }
  }

  "ValidateMessageTypeAction - arrivals" - {
    val validateMessageType = new MessageRequestRefiner()(implicitly)(MovementType.Arrival)

    for (messageType <- MessageType.arrivalMessages)
      s"${messageType.code}" - {
        "must execute the block when passed in a valid arrival message type" in {
          val result = validateMessageType.invokeBlock(
            createRequest(messageType),
            _ => Future.successful(Created)
          )
          status(result) mustEqual CREATED
        }

        "must return NotImplemented when passed in an invalid test message" in {
          val invalidMessageType = Gen.oneOf(MessageType.departureMessages).sample.value

          val result = validateMessageType.invokeBlock(
            createRequest(invalidMessageType),
            _ => Future.successful(Created)
          )
          status(result) mustEqual NOT_IMPLEMENTED
        }
      }

  }
}
