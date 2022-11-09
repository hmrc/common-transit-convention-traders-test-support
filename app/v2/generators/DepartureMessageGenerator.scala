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

import com.google.inject.ImplementedBy
import com.google.inject.Inject
import utils.Strings
import v2.models.MessageType
import v2.models.MessageType.MRNAllocated
import v2.models.MessageType.PositiveAcknowledgement
import v2.models.XMLMessage

import java.time.Clock

@ImplementedBy(classOf[DepartureMessageGeneratorImpl])
trait DepartureMessageGenerator extends MessageGenerator

class DepartureMessageGeneratorImpl @Inject()(clock: Clock) extends Generators with DepartureMessageGenerator {

  override protected def generateWithCorrelationId(correlationId: String): PartialFunction[MessageType, XMLMessage] = {
    case PositiveAcknowledgement => generateIE928Message(correlationId)
    case MRNAllocated            => generateIE028Message(correlationId)
  }

  private def generateIE928Message(correlationId: String): XMLMessage =
    XMLMessage(
      <ncts:CC928C xmlns:ncts="http://ncts.dgtaxud.ec" PhaseID="NCTS5.0">
        <messageSender>{Strings.alphanumeric(1, 35)}</messageSender>
        <messageRecipient>{correlationId}</messageRecipient>
        <preparationDateAndTime>{generateLocalDateTime()}</preparationDateAndTime>
        <messageIdentification>{Strings.alphanumeric(1, 35)}</messageIdentification>
        <messageType>CC928C</messageType>
        <correlationIdentifier>{Strings.alphanumeric(1, 35)}</correlationIdentifier>
        <TransitOperation>
          <LRN>{Strings.alphanumeric(2, 22)}</LRN>
        </TransitOperation>
        <CustomsOfficeOfDeparture>
          <referenceNumber>{Strings.referenceNumber()}</referenceNumber>
        </CustomsOfficeOfDeparture>
        <HolderOfTheTransitProcedure>
          <identificationNumber>{Strings.alphanumeric(8, 17)}</identificationNumber>
          <TIRHolderIdentificationNumber>{Strings.alphanumeric(8, 17)}</TIRHolderIdentificationNumber>
          <name>{Strings.alphanumeric(8, 70)}</name>
          <Address>
            <streetAndNumber>{Strings.alphanumeric(8, 70)}</streetAndNumber>
            <postcode>{Strings.alphanumeric(6, 17)}</postcode>
            <city>{Strings.alphanumeric(3, 35)}</city>
            <country>{Strings.alpha(2).toUpperCase}</country>
          </Address>
        </HolderOfTheTransitProcedure>
      </ncts:CC928C>
    )

  private def generateIE028Message(correlationId: String): XMLMessage =
    XMLMessage(
      <ncts:CC028C xmlns:ncts="http://ncts.dgtaxud.ec" PhaseID="NCTS5.0">
        <messageSender>{Strings.alphanumeric(35)}</messageSender>
        <messageRecipient>{correlationId}</messageRecipient>
        <preparationDateAndTime>{generateLocalDateTime()}</preparationDateAndTime>
        <messageIdentification>{Strings.alphanumeric(35)}</messageIdentification>
        <messageType>CC028C</messageType>
        <!--Optional:-->
        <correlationIdentifier>{correlationId}</correlationIdentifier>
        <TransitOperation>
          <LRN>{Strings.alphanumeric(2, 22)}</LRN>
          <MRN>{Strings.mrn()}</MRN>
          <declarationAcceptanceDate>{generateLocalDate()}</declarationAcceptanceDate>
        </TransitOperation>
        <CustomsOfficeOfDeparture>
          <referenceNumber>{Strings.referenceNumber()}</referenceNumber>
        </CustomsOfficeOfDeparture>
        <HolderOfTheTransitProcedure/>
      </ncts:CC028C>
    )
}
