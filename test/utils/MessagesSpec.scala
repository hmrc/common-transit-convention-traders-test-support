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

package utils

import data.TestXml
import models.XSDFile
import org.scalacheck.Gen
import org.scalatest.StreamlinedXmlEquality
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import services.XmlValidationService
import utils.Messages.GenerateMessage

import scala.xml.NodeSeq

class MessagesSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with TestXml with StreamlinedXmlEquality {

  val arrivalMessagesGenerator  = Gen.oneOf(Messages.Arrival.SupportedMessageTypes.values.toSeq)
  val departureMessageGenerator = Gen.oneOf(Messages.Departure.SupportedMessageTypes.values.toSeq)

  private val xmlValidationService: XmlValidationService = new XmlValidationService()

  "Messages" - {

    "Arrival" - {

      "SupportedMessageTypes" - {

        "must pass XSD validation" in {
          forAll(arrivalMessagesGenerator, minSuccessful(1000)) {
            code: GenerateMessage =>
              val message: NodeSeq = code()
              val result           = xmlValidationService.validate(message.toString(), XSDFile.Arrivals.get(message.head.label).get)
              result.isRight mustBe true
          }
        }
      }
    }

    "Departure" - {

      "SupportedMessageTypes" - {

        "must pass XSD validation" in {
          forAll(departureMessageGenerator, minSuccessful(1000)) {
            code: GenerateMessage =>
              val message: NodeSeq = code()
              val result           = xmlValidationService.validate(message.toString(), XSDFile.Departures.get(message.head.label).get)
              result.isRight mustBe true
          }
        }
      }
    }
  }

}
