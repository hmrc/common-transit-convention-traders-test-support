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

package v2.generators

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import v2.models.DepartureId
import v2.models.MessageType
import v2.models.MessageType.MRNAllocated

import java.time.Clock
import scala.xml.NodeSeq

class DepartureMessageGeneratorSpec extends AnyFreeSpec with Matchers {

  import Setup._
  "A generator" - {
    "when supplied with message type MRNAllocated" - {
      "should produce an IE028 Message" - {

        val traderChannelResponse = messages(MRNAllocated)

        // header
        val messageSender          = (traderChannelResponse \ "CC028C" \ "messageSender").text
        val messageRecipient       = (traderChannelResponse \ "CC028C" \ "messageRecipient").text
        val preparationDateAndTime = (traderChannelResponse \ "CC028C" \ "preparationDateAndTime").text
        val messageIdentification  = (traderChannelResponse \ "CC028C" \ "messageIdentification").text
        val messageType            = (traderChannelResponse \ "CC028C" \ "messageType").text
        val correlationIdentifier  = (traderChannelResponse \ "CC028C" \ "correlationIdentifier").text

        // TransitOperation
        val mrn                       = (traderChannelResponse \ "CC028C" \ "TransitOperation" \ "MRN").text
        val declarationAcceptanceDate = (traderChannelResponse \ "CC028C" \ "TransitOperation" \ "declarationAcceptanceDate").text

        // CustomsOfficeOfDeparture
        val referenceNumber = (traderChannelResponse \ "CC028C" \ "CustomsOfficeOfDeparture" \ "referenceNumber").text

        // Header
        messageSender should fullyMatch regex messageSenderPattern
        messageRecipient should fullyMatch regex messageRecipientPattern
        preparationDateAndTime should fullyMatch regex preparationDateAndTimePattern
        messageIdentification should fullyMatch regex messageIdentificationPattern
        messageType should fullyMatch regex messageTypePattern
        correlationIdentifier should fullyMatch regex correlationIdentifierPattern

        // TransitOperation
        messages.isDefinedAt(MRNAllocated) should be(true)
        mrn should fullyMatch regex mrnPattern
        declarationAcceptanceDate should fullyMatch regex datePattern

        // CustomsOfficeOfDeparture
        referenceNumber should fullyMatch regex referenceNumberPattern

      }
    }
  }

  object Setup {
    val clock                                           = Clock.systemUTC()
    val ut                                              = new DepartureMessageGenerator(clock)
    val messages: PartialFunction[MessageType, NodeSeq] = ut.generate(DepartureId("departureId"))

    // Regex patterns from .xsd
    val messageSenderPattern          = ".{1,35}"
    val messageRecipientPattern       = "departureId-0000000000000000"
    val preparationDateAndTimePattern = """\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}"""
    val messageIdentificationPattern  = ".{1,35}"
    val messageTypePattern            = "CC028C"
    val correlationIdentifierPattern  = messageRecipientPattern
    val mrnPattern                    = "([2][4-9]|[3-9][0-9])[A-Z]{2}[A-Z0-9]{12}[J-M][0-9]"
    val datePattern                   = "\\d{4}-\\d{2}-\\d{2}(\\.\\d+)?"
    val referenceNumberPattern        = "[A-Z]{2}[A-Z0-9]{6}"
  }

}
