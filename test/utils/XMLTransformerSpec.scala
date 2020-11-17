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

import java.time.LocalDateTime

import data.TestXml
import org.scalacheck.Gen
import models.ArrivalWithMessages
import models.DepartureWithMessages
import models.MovementMessage
import org.scalatest.StreamlinedXmlEquality
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import utils.Messages.GenerateMessage

class XMLTransformerSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with TestXml with StreamlinedXmlEquality {

  val arrivalMessagesGenerator: Gen[GenerateMessage]  = Gen.oneOf(Messages.Arrival.SupportedMessageTypes.values.toSeq)
  val departureMessageGenerator: Gen[GenerateMessage] = Gen.oneOf(Messages.Departure.SupportedMessageTypes.values.toSeq)

  "XMLTransformer" - {

    "populateMesRecMES6" - {

      "for arrival messages" - {

        "must populate MesRecMES6 from MesSenMES3 in submitted IE007 xml" in {
          val arrivalWithMessages = ArrivalWithMessages(1,
                                                        "loc",
                                                        "messageLoc",
                                                        "mrn",
                                                        "status",
                                                        LocalDateTime.now(),
                                                        LocalDateTime.now(),
                                                        Seq(MovementMessage("/1", LocalDateTime.now(), "IE007", CC007A)))

          forAll(arrivalMessagesGenerator) {
            code: GenerateMessage =>
              val message = code()
              val updated = XMLTransformer.populateMesRecMES6(message, "IE007", arrivalWithMessages.messages)
              (updated \\ "MesRecMES6").text mustEqual (CC007A \\ "MesSenMES3").text
          }
        }

        "must populate MesRecMES6 from MesSenMES3 in most recently submitted IE007 xml" in {
          val arrivalWithMessages = ArrivalWithMessages(
            1,
            "loc",
            "messageLoc",
            "mrn",
            "status",
            LocalDateTime.now(),
            LocalDateTime.now(),
            Seq(
              MovementMessage("/1", LocalDateTime.now(), "IE007", CC007A),
              MovementMessage("/2", LocalDateTime.now(), "IE007", CC007Av2)
            )
          )

          forAll(arrivalMessagesGenerator) {
            code: GenerateMessage =>
              val message = code()
              val updated = XMLTransformer.populateMesRecMES6(message, "IE007", arrivalWithMessages.messages)
              (updated \\ "MesRecMES6").text mustEqual (CC007Av2 \\ "MesSenMES3").text
              (updated \\ "MesRecMES6").text must not equal (CC007A \\ "MesSenMES3").text
          }
        }

        "leave MesRecMES6 field alone if IE007 is not present in arrival messages" in {
          val arrivalWithMessages = ArrivalWithMessages(
            1,
            "loc",
            "messageLoc",
            "mrn",
            "status",
            LocalDateTime.now(),
            LocalDateTime.now(),
            Seq.empty
          )

          forAll(arrivalMessagesGenerator) {
            code: GenerateMessage =>
              val message = code()
              val updated = XMLTransformer.populateMesRecMES6(message, "IE007", arrivalWithMessages.messages)
              (updated \\ "MesRecMES6").text mustEqual (message \\ "MesRecMES6").text
              (updated \\ "MesRecMES6").text must not equal (CC007Av2 \\ "MesSenMES3").text
              (updated \\ "MesRecMES6").text must not equal (CC007A \\ "MesSenMES3").text
          }
        }
      }

      "for departure messages" - {

        "must populate MesRecMES6 from MesSenMES3 in submitted IE015 xml" in {
          val departureWithMessages = DepartureWithMessages(1,
                                                            "loc",
                                                            "messageLoc",
                                                            Some("mrn"),
                                                            "ref",
                                                            "status",
                                                            LocalDateTime.now(),
                                                            LocalDateTime.now(),
                                                            Seq(MovementMessage("/1", LocalDateTime.now(), "IE015", CC015B)))

          forAll(departureMessageGenerator) {
            code: GenerateMessage =>
              val message = code()
              val updated = XMLTransformer.populateMesRecMES6(message, "IE015", departureWithMessages.messages)
              (updated \\ "MesRecMES6").text mustEqual (CC015B \\ "MesSenMES3").text
          }
        }

        "must populate MesRecMES6 from MesSenMES3 in most recently submitted IE015 xml" in {
          val departureWithMessages = DepartureWithMessages(
            1,
            "loc",
            "messageLoc",
            Some("mrn"),
            "ref",
            "status",
            LocalDateTime.now(),
            LocalDateTime.now(),
            Seq(
              MovementMessage("/1", LocalDateTime.now(), "IE015", CC015B),
              MovementMessage("/2", LocalDateTime.now(), "IE015", CC015Bv2)
            )
          )

          forAll(departureMessageGenerator) {
            code: GenerateMessage =>
              val message = code()
              val updated = XMLTransformer.populateMesRecMES6(message, "IE015", departureWithMessages.messages)
              (updated \\ "MesRecMES6").text mustEqual (CC015Bv2 \\ "MesSenMES3").text
              (updated \\ "MesRecMES6").text must not equal (CC015B \\ "MesSenMES3").text
          }
        }

        "leave MesRecMES6 field alone if IE015 is not present in arrival messages" in {
          val departureWithMessages = DepartureWithMessages(
            1,
            "loc",
            "messageLoc",
            Some("mrn"),
            "ref",
            "status",
            LocalDateTime.now(),
            LocalDateTime.now(),
            Seq.empty
          )

          forAll(departureMessageGenerator) {
            code: GenerateMessage =>
              val message = code()
              val updated = XMLTransformer.populateMesRecMES6(message, "IE015", departureWithMessages.messages)
              (updated \\ "MesRecMES6").text mustEqual (message \\ "MesRecMES6").text
              (updated \\ "MesRecMES6").text must not equal (CC015Bv2 \\ "MesSenMES3").text
              (updated \\ "MesRecMES6").text must not equal (CC015B \\ "MesSenMES3").text
          }
        }
      }
    }
  }

}
