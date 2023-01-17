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

package models

import base.SpecBase
import models.MessageType.ArrivalNegativeAcknowledgement
import models.MessageType.XMLSubmissionNegativeAcknowledgement
import models.generation.TestMessage
import org.scalatest.OptionValues
import play.api.libs.json.Json

class TestMessageSpec extends SpecBase with OptionValues {

  "TestMessage" - {

    "must return XMLSubmissionNegativeAcknowledgement when given a 917 with DEP source" in {

      val departure917 = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE917",
          |         "source": "DEP"
          |     }
          | }""".stripMargin
      )

      val expectedResult = TestMessage(XMLSubmissionNegativeAcknowledgement)

      departure917.validate[TestMessage].asOpt.value mustBe expectedResult
    }

    "must return ArrivalNegativeAcknowledgement when given a 917 with ARR source" in {

      val arrival917 = Json.parse(
        """{
          |     "message": {
          |         "messageType": "IE917",
          |         "source": "ARR"
          |     }
          | }""".stripMargin
      )

      val expectedResult = TestMessage(ArrivalNegativeAcknowledgement)

      arrival917.validate[TestMessage].asOpt.value mustBe expectedResult
    }

    "must return other message types when not a 917 message" in {

      MessageType.values.map {
        messageType =>
          val message = Json.parse(
            s"""{
               |     "message": {
               |         "messageType": "${messageType.code}"
               |     }
               | }""".stripMargin
          )

          message.validate[TestMessage].asOpt.value mustBe TestMessage(messageType)
      }
    }
  }
}
