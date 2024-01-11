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

package v2.controllers.actions

import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContent
import play.api.mvc.Results.Created
import play.api.test.FakeRequest
import play.api.test.Helpers._
import v2.models.EORINumber
import v2.models.MessageType
import v2.models.request.MessageRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ValidateMessageTypeActionSpec extends AnyFreeSpec with Matchers with OptionValues with ScalaFutures with MockitoSugar with BeforeAndAfterEach {

  private def createRequest(messageType: MessageType): MessageRequest[AnyContent] =
    MessageRequest(FakeRequest(), EORINumber("a"), messageType)

  "ValidateMessageTypeAction - departures" - {
    val validateMessageType = new ValidateMessageTypeAction(MessageType.departureMessages)

    for (messageType <- MessageType.departureMessages)
      s"${messageType.code}" - {
        "must execute the block when passed in a valid departure message type" in {
          val result = validateMessageType.invokeBlock(
            createRequest(messageType),
            {
              _: MessageRequest[AnyContent] =>
                Future.successful(Created)
            }
          )
          status(result) mustEqual CREATED
        }

        "must return NotImplemented when passed in an invalid test message" in {
          val invalidMessageType = Gen.oneOf(MessageType.arrivalMessages).sample.value

          val result = validateMessageType.invokeBlock(
            createRequest(invalidMessageType),
            {
              _: MessageRequest[AnyContent] =>
                Future.successful(Created)
            }
          )
          status(result) mustEqual NOT_IMPLEMENTED
        }
      }
  }

  "ValidateMessageTypeAction - arrivals" - {
    val validateMessageType = new ValidateMessageTypeAction(MessageType.arrivalMessages)

    for (messageType <- MessageType.arrivalMessages)
      s"${messageType.code}" - {
        "must execute the block when passed in a valid arrival message type" in {
          val result = validateMessageType.invokeBlock(
            createRequest(messageType),
            {
              _: MessageRequest[AnyContent] =>
                Future.successful(Created)
            }
          )
          status(result) mustEqual CREATED
        }

        "must return NotImplemented when passed in an invalid test message" in {
          val invalidMessageType = Gen.oneOf(MessageType.departureMessages).sample.value

          val result = validateMessageType.invokeBlock(
            createRequest(invalidMessageType),
            {
              _: MessageRequest[AnyContent] =>
                Future.successful(Created)
            }
          )
          status(result) mustEqual NOT_IMPLEMENTED
        }
      }

  }
}
