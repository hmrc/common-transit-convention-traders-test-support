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

import com.google.inject.Inject
import config.Constants
import generators.Generator
import v2.models.DepartureId
import v2.models.MessageType
import v2.models.MessageType.MRNAllocated
import v2.models.MessageType.PositiveAcknowledgement
import utils.Strings

import java.time.Clock
import scala.xml.NodeSeq

class DepartureMessageGenerator @Inject()(clock: Clock) extends Generator(clock) {

  def correlationId(departureId: DepartureId) = s"${departureId.value}-${Constants.DefaultTriggerId}"

  def generate(departureId: DepartureId): PartialFunction[MessageType, NodeSeq] = {
    case PositiveAcknowledgement => generateIE928Message(correlationId(departureId))
    case MRNAllocated            => generateIE028Message(correlationId(departureId))
  }

  private def generateIE928Message(correlationId: String): NodeSeq =
    <TraderChannelResponse>
      <CC928C PhaseID="NCTS5.0">
        <messageSender>{Strings.alphanumeric(1, 35)}</messageSender>
        <messageRecipient>{correlationId}</messageRecipient>
        <preparationDateAndTime>{localDateTime}</preparationDateAndTime>
        <messageIdentification>{Strings.alphanumeric(1, 35)}</messageIdentification>
        <messageType>CC928C</messageType>
        <correlationIdentifier>{Strings.alphanumeric(1, 35)}</correlationIdentifier>
        <TransitOperation>
          <LRN>{Strings.alphanumeric(2, 22)}</LRN>
        </TransitOperation>
        <CustomsOfficeOfDeparture>
          <referenceNumber>{Strings.alpha(2)}{Strings.alphanumeric(6)}</referenceNumber>
        </CustomsOfficeOfDeparture>
        <HolderOfTheTransitProcedure>
          <identificationNumber>{Strings.alphanumeric(8, 17)}</identificationNumber>
          <TIRHolderIdentificationNumber>{Strings.alphanumeric(8, 17)}</TIRHolderIdentificationNumber>
          <name>{Strings.alphanumeric(8, 70)}</name>
          <Address>
            <streetAndNumber>{Strings.alphanumeric(8, 70)}</streetAndNumber>
            <city>{Strings.alphanumeric(3, 35)}</city>
            <postCode>{Strings.alphanumeric(6, 17)}</postCode>
            <country>{Strings.alpha(2)}</country>
          </Address>
        </HolderOfTheTransitProcedure>
      </CC928C>
    </TraderChannelResponse>

  private def generateIE028Message(correlationId: String): NodeSeq =
    <TraderChannelResponse>
      <CC028C>
        <messageSender>{Strings.alphanumeric(35)}</messageSender>
        <messageRecipient>{correlationId}</messageRecipient>
        <preparationDateAndTime>{isoDateTime}</preparationDateAndTime>
        <messageIdentification>{Strings.alphanumeric(35)}</messageIdentification>
        <messageType>CC028C</messageType>
        <!--Optional:-->
        <correlationIdentifier>{correlationId}</correlationIdentifier>
        <TransitOperation>
          <LRN>{Strings.alphanumeric(2, 22)}</LRN>
          <MRN>{Strings.mrn()}</MRN>
          <declarationAcceptanceDate>{isoDate}</declarationAcceptanceDate>
        </TransitOperation>
        <CustomsOfficeOfDeparture>
          <referenceNumber>{Strings.referenceNumber()}</referenceNumber>
        </CustomsOfficeOfDeparture>
        <HolderOfTheTransitProcedure/>
      </CC028C>
  </TraderChannelResponse>
}
