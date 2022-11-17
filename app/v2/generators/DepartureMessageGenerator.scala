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
import v2.models.MessageType.AmendmentAcceptance
import v2.models.MessageType.MRNAllocated
import v2.models.MessageType.PositiveAcknowledgement
import v2.models.MessageType.ReleaseForTransit
import v2.models.XMLMessage

import java.time.Clock
import utils.Strings.alpha
import utils.Strings.alphanumeric
import utils.Strings.alphanumericCapital
import utils.Strings.decimalNumber
import utils.Strings.mrn
import utils.Strings.num
import utils.Strings.num1
import utils.Strings.numeric
import utils.Strings.referenceNumber
import utils.Strings.zeroOrOne

@ImplementedBy(classOf[DepartureMessageGeneratorImpl])
trait DepartureMessageGenerator extends MessageGenerator

class DepartureMessageGeneratorImpl @Inject() (clock: Clock) extends Generators with DepartureMessageGenerator {

  override protected def generateWithCorrelationId(correlationId: String): PartialFunction[MessageType, XMLMessage] = {
    case AmendmentAcceptance     => generateIE004Message(correlationId)
    case PositiveAcknowledgement => generateIE928Message(correlationId)
    case MRNAllocated            => generateIE028Message(correlationId)
    case ReleaseForTransit       => generateIE029Message(correlationId)
  }

  private def generateIE004Message(correlationId: String): XMLMessage =
    XMLMessage(
      <ncts:CC004C PhaseID="NCTS5.0" xmlns:ncts="http://ncts.dgtaxud.ec">
        <messageSender>{Strings.alphanumeric(1, 35)}</messageSender>
        <messageRecipient>{correlationId}</messageRecipient>
        <preparationDateAndTime>{generateLocalDateTime()}</preparationDateAndTime>
        <messageIdentification>{Strings.alphanumeric(1, 35)}</messageIdentification>
        <messageType>CC004C</messageType>
        <!--Optional:-->
        <correlationIdentifier>{correlationId}</correlationIdentifier>
        <TransitOperation>
          <!--Optional:-->
          <LRN>{Strings.alphanumeric(1, 22)}</LRN>
          <!--Optional:-->
          <MRN>{Strings.mrn()}</MRN>
          <amendmentSubmissionDateAndTime>{generateLocalDateTime()}</amendmentSubmissionDateAndTime>
          <amendmentAcceptanceDateAndTime>{generateLocalDateTime()}</amendmentAcceptanceDateAndTime>
        </TransitOperation>
        <CustomsOfficeOfDeparture>
          <referenceNumber>{Strings.referenceNumber()}</referenceNumber>
        </CustomsOfficeOfDeparture>
        <HolderOfTheTransitProcedure>
          <!--Optional:-->
          <identificationNumber>{Strings.alphanumeric(1, 17)}</identificationNumber>
          <!--Optional:-->
          <TIRHolderIdentificationNumber>{Strings.alphanumeric(1, 17)}</TIRHolderIdentificationNumber>
          <!--Optional:-->
          <name>{Strings.alphanumeric(1, 70)}</name>
          <!--Optional:-->
          <Address>
            <streetAndNumber>{Strings.alphanumeric(2, 70)}</streetAndNumber>
            <!--Optional:-->
            <postcode>{Strings.alphanumeric(2, 17)}</postcode>
            <city>{Strings.alphanumeric(2, 35)}</city>
            <country>{Strings.alpha(2).toUpperCase}</country>
          </Address>
        </HolderOfTheTransitProcedure>
      </ncts:CC004C>
    )

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

  private def generateIE029Message(correlationId: String): XMLMessage =
    XMLMessage(
      <ncts:CC029C xmlns:ncts="http://ncts.dgtaxud.ec" PhaseID="NCTS5.0">
        <messageSender>{alphanumeric(35)}</messageSender>
        <messageRecipient>{correlationId}</messageRecipient>
        <preparationDateAndTime>{generateLocalDateTime()}</preparationDateAndTime>
        <messageIdentification>{alphanumeric(35)}</messageIdentification>
        <messageType>CC029C</messageType>
        <correlationIdentifier>{correlationId}</correlationIdentifier>
        <TransitOperation>
          <LRN>{alphanumeric(1, 22)}</LRN>
          <MRN>{mrn()}</MRN>
          <declarationType>{alphanumeric(1, 5)}</declarationType>
          <additionalDeclarationType>{alpha(1)}</additionalDeclarationType>
          <declarationAcceptanceDate>{generateLocalDate()}</declarationAcceptanceDate>
          <releaseDate>{generateLocalDate()}</releaseDate>
          <security>{num(1)}</security>
          <reducedDatasetIndicator>{zeroOrOne}</reducedDatasetIndicator>
          <specificCircumstanceIndicator>{alphanumeric(3)}</specificCircumstanceIndicator>
          <bindingItinerary>{zeroOrOne}</bindingItinerary>
        </TransitOperation>
        <CustomsOfficeOfDeparture>
          <referenceNumber>{referenceNumber()}</referenceNumber>
        </CustomsOfficeOfDeparture>
        <CustomsOfficeOfDestinationDeclared>
          <referenceNumber>{referenceNumber()}</referenceNumber>
        </CustomsOfficeOfDestinationDeclared>
        <HolderOfTheTransitProcedure>
          <identificationNumber>{alphanumeric(8, 17)}</identificationNumber>
          <TIRHolderIdentificationNumber>{alphanumeric(8, 17)}</TIRHolderIdentificationNumber>
          <name>{alphanumeric(8, 70)}</name>
          <Address>
            <streetAndNumber>{alphanumeric(8, 70)}</streetAndNumber>
            <postcode>{alphanumeric(6, 17)}</postcode>
            <city>{alphanumeric(3, 35)}</city>
            <country>{alpha(2).toUpperCase}</country>
          </Address>
        </HolderOfTheTransitProcedure>
        <Guarantee>
          <sequenceNumber>{numeric(1, 5)}</sequenceNumber>
          <guaranteeType>{alphanumericCapital(1)}</guaranteeType>
        </Guarantee>
        <Consignment>
          <containerIndicator>{zeroOrOne}</containerIndicator>
          <grossMass>{decimalNumber(16, 6)}</grossMass>
          <PlaceOfLoading>
            <country>{alpha(2).toUpperCase}</country>
          </PlaceOfLoading>
          <HouseConsignment>
            <sequenceNumber>{numeric(1, 5)}</sequenceNumber>
            <grossMass>{decimalNumber(16, 6)}</grossMass>
            <ConsignmentItem>
              <goodsItemNumber>{numeric(5)}</goodsItemNumber>
              <declarationGoodsItemNumber>{num1(2)}</declarationGoodsItemNumber>
              <Commodity>
                <descriptionOfGoods>{alphanumeric(1, 512)}</descriptionOfGoods>
                <GoodsMeasure>
                  <grossMass>{decimalNumber(16, 6)}</grossMass>
                </GoodsMeasure>
              </Commodity>
              <Packaging>
                <sequenceNumber>{numeric(1, 5)}</sequenceNumber>
                <typeOfPackages>{alphanumeric(2)}</typeOfPackages>
              </Packaging>
            </ConsignmentItem>
          </HouseConsignment>
        </Consignment>
      </ncts:CC029C>
    )
}
