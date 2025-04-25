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

package v2_1.generators

import com.google.inject.ImplementedBy
import v2_1.models.MessageType.GoodsReleaseNotification
import v2_1.models.MessageType.RejectionFromOfficeOfDestination
import v2_1.models.MessageType.UnloadingPermission
import v2_1.models.MessageType
import v2_1.models.XMLMessage
import v2_1.utils.Strings.*

@ImplementedBy(classOf[ArrivalMessageGeneratorImpl])
trait ArrivalMessageGenerator extends MessageGenerator

class ArrivalMessageGeneratorImpl extends Generators with ArrivalMessageGenerator {

  override protected def generateWithCorrelationId(correlationId: String): PartialFunction[MessageType, XMLMessage] = {
    case GoodsReleaseNotification         => generateIE025Message(correlationId)
    case UnloadingPermission              => generateIE043Message(correlationId)
    case RejectionFromOfficeOfDestination => generateIE057Message(correlationId)
  }

  private def generateIE025Message(correlationId: String): XMLMessage =
    XMLMessage(
      <ncts:CC025C xmlns:ncts="http://ncts.dgtaxud.ec" PhaseID="NCTS5.0">
        <messageSender>{alphanumeric(1, 35)}</messageSender>
        <messageRecipient>{correlationId}</messageRecipient>
        <preparationDateAndTime>{generateLocalDateTime()}</preparationDateAndTime>
        <messageIdentification>{alphanumeric(1, 35)}</messageIdentification>
        <messageType>CC025C</messageType>
        <correlationIdentifier>{alphanumeric(1, 35)}</correlationIdentifier>
        <TransitOperation>
          <MRN>{mrn()}</MRN>
          <releaseDate>{generateLocalDate()}</releaseDate>
          <releaseIndicator>0</releaseIndicator>
        </TransitOperation>
        <CustomsOfficeOfDestinationActual>
          <referenceNumber>{referenceNumber()}</referenceNumber>
        </CustomsOfficeOfDestinationActual>
        <TraderAtDestination>
          <identificationNumber>{alphanumeric(8, 17)}</identificationNumber>
        </TraderAtDestination>
        <!--Optional:-->
        <Consignment>
          <!--1 to 1999 repetitions:-->
          <HouseConsignment>
            <sequenceNumber>{between1And99999}</sequenceNumber>
            <releaseType>0</releaseType>
            <!--0 to 999 repetitions:-->
            <ConsignmentItem>
              <goodsItemNumber>{between1And99999}</goodsItemNumber>
              <declarationGoodsItemNumber>{generateDeclarationGoodsNumber()}</declarationGoodsItemNumber>
              <releaseType>0</releaseType>
              <Commodity>
                <descriptionOfGoods>{alphanumeric(64)}</descriptionOfGoods>
                <!--Optional:-->
                <cusCode>{alphanumeric(9)}</cusCode>
                <!--Optional:-->
                <CommodityCode>
                  <harmonizedSystemSubHeadingCode>{alphanumeric(6)}</harmonizedSystemSubHeadingCode>
                </CommodityCode>
                <!--0 to 99 repetitions:-->
                <DangerousGoods>
                  <sequenceNumber>{between1And99999}</sequenceNumber>
                  <UNNumber>{alphanumeric(4)}</UNNumber>
                </DangerousGoods>
              </Commodity>
              <!--1 to 99 repetitions:-->
              <Packaging>
                <sequenceNumber>{between1And99999}</sequenceNumber>
                <typeOfPackages>{alphanumeric(2)}</typeOfPackages>
                <!--Optional:-->
                <numberOfPackages>0</numberOfPackages>
                <!--Optional:-->
                <shippingMarks>{alphanumeric(64)}</shippingMarks>
              </Packaging>
            </ConsignmentItem>
          </HouseConsignment>
        </Consignment>
      </ncts:CC025C>
    )

  private def generateIE043Message(correlationId: String): XMLMessage =
    XMLMessage(
      <ncts:CC043C xmlns:ncts="http://ncts.dgtaxud.ec" PhaseID="NCTS5.0">
        <messageSender>{alphanumeric(1, 35)}</messageSender>
        <messageRecipient>{correlationId}</messageRecipient>
        <preparationDateAndTime>{generateLocalDateTime()}</preparationDateAndTime>
        <messageIdentification>{alphanumeric(1, 35)}</messageIdentification>
        <messageType>CC043C</messageType>
        <correlationIdentifier>{alphanumeric(1, 35)}</correlationIdentifier>
        <TransitOperation>
          <MRN>{mrn()}</MRN>
          <!--Optional:-->
          <declarationType>{alphanumeric(5)}</declarationType>
          <!--Optional:-->
          <declarationAcceptanceDate>{generateLocalDate()}</declarationAcceptanceDate>
          <security>{numeric(1)}</security>
          <reducedDatasetIndicator>{zeroOrOne()}</reducedDatasetIndicator>
        </TransitOperation>
        <CustomsOfficeOfDestinationActual>
          <referenceNumber>{referenceNumber()}</referenceNumber>
        </CustomsOfficeOfDestinationActual>
        <!--Optional:-->
        <HolderOfTheTransitProcedure>
          <!--Optional:-->
          <identificationNumber>{alphanumeric(1, 17)}</identificationNumber>
          <!--Optional:-->
          <TIRHolderIdentificationNumber>{alphanumeric(8, 17)}</TIRHolderIdentificationNumber>
          <name>{alphanumeric(8, 70)}</name>
          <Address>
            <streetAndNumber>{alphanumeric(8, 70)}</streetAndNumber>
            <!--Optional:-->
            <postcode>{alphanumeric(6, 17)}</postcode>
            <city>{alphanumeric(3, 35)}</city>
            <country>{alpha(2).toUpperCase}</country>
          </Address>
        </HolderOfTheTransitProcedure>
        <TraderAtDestination>
          <identificationNumber>{alphanumeric(1, 17)}</identificationNumber>
        </TraderAtDestination>
        <!--Optional:-->
        <CTLControl>
          <continueUnloading>{between1And9}</continueUnloading>
        </CTLControl>
        <!--Optional:-->
        <Consignment>
          <!--Optional:-->
          <countryOfDestination>{country()}</countryOfDestination>
          <containerIndicator>{zeroOrOne()}</containerIndicator>
          <!--Optional:-->
          <inlandModeOfTransport>{numeric(1)}</inlandModeOfTransport>
          <!--Optional:-->
          <grossMass>12345.6789</grossMass>
          <!--Optional:-->
          <Consignor>
            <!--Optional:-->
            <identificationNumber>{alphanumeric(1, 17)}</identificationNumber>
            <!--Optional:-->
            <name>{alphanumeric(8, 70)}</name>
            <!--Optional:-->
            <Address>
              <streetAndNumber>{alphanumeric(2, 70)}</streetAndNumber>
              <!--Optional:-->
              <postcode>{alphanumeric(6, 17)}</postcode>
              <city>{alphanumeric(3, 35)}</city>
              <country>{alpha(2).toUpperCase}</country>
            </Address>
          </Consignor>
          <!--Optional:-->
          <Consignee>
            <!--Optional:-->
            <identificationNumber>{alphanumeric(1, 17)}</identificationNumber>
            <!--Optional:-->
            <name>{alphanumeric(8, 70)}</name>
            <!--Optional:-->
            <Address>
              <streetAndNumber>{alphanumeric(8, 70)}</streetAndNumber>
              <!--Optional:-->
              <postcode>{alphanumeric(6, 17)}</postcode>
              <city>{alphanumeric(3, 35)}</city>
              <country>{alpha(2).toUpperCase}</country>
            </Address>
          </Consignee>
          <!--0 to 9999 repetitions:-->
          <TransportEquipment>
            <sequenceNumber>{between1And99999}</sequenceNumber>
            <!--Optional:-->
            <containerIdentificationNumber>{alphanumeric(8, 17)}</containerIdentificationNumber>
            <numberOfSeals>{numericNonZeroStart(1, 4)}</numberOfSeals>
            <!--0 to 99 repetitions:-->
            <Seal>
              <sequenceNumber>{between1And99999}</sequenceNumber>
              <identifier>{alphanumeric(8, 20)}</identifier>
            </Seal>
            <!--0 to 9999 repetitions:-->
            <GoodsReference>
              <sequenceNumber>{between1And99999}</sequenceNumber>
              <declarationGoodsItemNumber>{generateDeclarationGoodsNumber()}</declarationGoodsItemNumber>
            </GoodsReference>
          </TransportEquipment>
          <!--0 to 999 repetitions:-->
          <DepartureTransportMeans>
            <sequenceNumber>{between1And99999}</sequenceNumber>
            <typeOfIdentification>{numeric(2)}</typeOfIdentification>
            <identificationNumber>{alphanumeric(1, 35)}</identificationNumber>
            <nationality>{country()}</nationality>
          </DepartureTransportMeans>
          <!--0 to 9999 repetitions:-->
          <PreviousDocument>
            <sequenceNumber>{between1And99999}</sequenceNumber>
            <type>{alphanumeric(4)}</type>
            <referenceNumber>{alphanumeric(2, 70)}</referenceNumber>
            <!--Optional:-->
            <complementOfInformation>{alphanumeric(2, 35)}</complementOfInformation>
          </PreviousDocument>
          <!--0 to 99 repetitions:-->
          <SupportingDocument>
            <sequenceNumber>{between1And99999}</sequenceNumber>
            <type>{alphanumeric(4)}</type>
            <referenceNumber>{alphanumeric(8, 70)}</referenceNumber>
            <!--Optional:-->
            <complementOfInformation>{alphanumeric(8, 35)}</complementOfInformation>
          </SupportingDocument>
          <!--0 to 99 repetitions:-->
          <TransportDocument>
            <sequenceNumber>{between1And99999}</sequenceNumber>
            <type>{alphanumeric(4)}</type>
            <referenceNumber>{alphanumeric(8, 70)}</referenceNumber>
          </TransportDocument>
          <!--0 to 99 repetitions:-->
          <AdditionalReference>
            <sequenceNumber>{between1And99999}</sequenceNumber>
            <type>{alphanumeric(4)}</type>
            <!--Optional:-->
            <referenceNumber>{alphanumeric(8, 70)}</referenceNumber>
          </AdditionalReference>
          <!--0 to 99 repetitions:-->
          <AdditionalInformation>
            <sequenceNumber>{between1And99999}</sequenceNumber>
            <code>{alphanumeric(5)}</code>
            <!--Optional:-->
            <text>{alphanumeric(20, 512)}</text>
          </AdditionalInformation>
          <!--0 to 9 repetitions:-->
          <Incident>
            <sequenceNumber>{between1And99999}</sequenceNumber>
            <code>{numeric(1)}</code>
            <text>{alphanumeric(20, 512)}</text>
            <!--Optional:-->
            <Endorsement>
              <date>{generateLocalDate()}</date>
              <authority>{alphanumeric(20, 35)}</authority>
              <place>{alphanumeric(20, 35)}</place>
              <country>{alpha(2).toUpperCase}</country>
            </Endorsement>
            <Location>
              <qualifierOfIdentification>{alpha(1).toUpperCase}</qualifierOfIdentification>
              <!--Optional:-->
              <UNLocode>{alphanumeric(5, 17)}</UNLocode>
              <country>{alpha(2).toUpperCase}</country>
              <!--Optional:-->
              <GNSS>
                <latitude>90.000000</latitude>
                <longitude>180.000000</longitude>
              </GNSS>
              <!--Optional:-->
              <Address>
                <streetAndNumber>{alphanumeric(8, 70)}</streetAndNumber>
                <!--Optional:-->
                <postcode>{alphanumeric(6, 17)}</postcode>
                <city>{alphanumeric(3, 35)}</city>
              </Address>
            </Location>
            <!--0 to 9999 repetitions:-->
            <TransportEquipment>
              <sequenceNumber>{between1And99999}</sequenceNumber>
              <!--Optional:-->
              <containerIdentificationNumber>{alphanumeric(8, 17)}</containerIdentificationNumber>
              <!--Optional:-->
              <numberOfSeals>{numericNonZeroStart(1, 4)}</numberOfSeals>
              <!--0 to 99 repetitions:-->
              <Seal>
                <sequenceNumber>{between1And99999}</sequenceNumber>
                <identifier>{alphanumeric(5, 20)}</identifier>
              </Seal>
              <!--0 to 9999 repetitions:-->
              <GoodsReference>
                <sequenceNumber>{between1And99999}</sequenceNumber>
                <declarationGoodsItemNumber>{generateDeclarationGoodsNumber()}</declarationGoodsItemNumber>
              </GoodsReference>
            </TransportEquipment>
            <!--Optional:-->
            <Transhipment>
              <containerIndicator>{zeroOrOne()}</containerIndicator>
              <TransportMeans>
                <typeOfIdentification>{numeric(2)}</typeOfIdentification>
                <identificationNumber>{alphanumeric(1, 35)}</identificationNumber>
                <nationality>{alpha(2).toUpperCase}</nationality>
              </TransportMeans>
            </Transhipment>
          </Incident>
          <!--1 to 1999 repetitions:-->
          <HouseConsignment>
            <sequenceNumber>{between1And99999}</sequenceNumber>
            <countryOfDestination>{country()}</countryOfDestination>
            <grossMass>9876.54321</grossMass>
            <!--Optional:-->
            <securityIndicatorFromExportDeclaration>{numeric(1)}</securityIndicatorFromExportDeclaration>
            <!--Optional:-->
            <Consignor>
              <!--Optional:-->
              <identificationNumber>{alphanumeric(1, 17)}</identificationNumber>
              <!--Optional:-->
              <name>{alphanumeric(4, 70)}</name>
              <!--Optional:-->
              <Address>
                <streetAndNumber>{alphanumeric(8, 70)}</streetAndNumber>
                <!--Optional:-->
                <postcode>{alphanumeric(3, 17)}</postcode>
                <city>{alphanumeric(3, 35)}</city>
                <country>{alpha(2).toUpperCase}</country>
              </Address>
            </Consignor>
            <!--Optional:-->
            <Consignee>
              <!--Optional:-->
              <identificationNumber>{alphanumeric(1, 17)}</identificationNumber>
              <!--Optional:-->
              <name>{alphanumeric(2, 70)}</name>
              <!--Optional:-->
              <Address>
                <streetAndNumber>{alphanumeric(8, 70)}</streetAndNumber>
                <!--Optional:-->
                <postcode>{alphanumeric(3, 17)}</postcode>
                <city>{alphanumeric(3, 35)}</city>
                <country>{alpha(2).toUpperCase}</country>
              </Address>
            </Consignee>
            <!--0 to 999 repetitions:-->
            <DepartureTransportMeans>
              <sequenceNumber>{between1And99999}</sequenceNumber>
              <typeOfIdentification>{numeric(2)}</typeOfIdentification>
              <identificationNumber>{alphanumeric(1, 35)}</identificationNumber>
              <nationality>{alpha(2).toUpperCase}</nationality>
            </DepartureTransportMeans>
            <!--0 to 99 repetitions:-->
            <PreviousDocument>
              <sequenceNumber>{between1And99999}</sequenceNumber>
              <type>{alphanumeric(4)}</type>
              <referenceNumber>{alphanumeric(4, 70)}</referenceNumber>
              <!--Optional:-->
              <complementOfInformation>{alphanumeric(1, 35)}</complementOfInformation>
            </PreviousDocument>
            <!--0 to 99 repetitions:-->
            <SupportingDocument>
              <sequenceNumber>{between1And99999}</sequenceNumber>
              <type>{alphanumeric(4)}</type>
              <referenceNumber>{alphanumeric(1, 70)}</referenceNumber>
              <!--Optional:-->
              <complementOfInformation>{alphanumeric(1, 35)}</complementOfInformation>
            </SupportingDocument>
            <!--0 to 99 repetitions:-->
            <TransportDocument>
              <sequenceNumber>{between1And99999}</sequenceNumber>
              <type>{alphanumeric(4)}</type>
              <referenceNumber>{alphanumeric(1, 70)}</referenceNumber>
            </TransportDocument>
            <!--0 to 99 repetitions:-->
            <AdditionalReference>
              <sequenceNumber>{between1And99999}</sequenceNumber>
              <type>{alphanumeric(4)}</type>
              <!--Optional:-->
              <referenceNumber>{alphanumeric(1, 70)}</referenceNumber>
            </AdditionalReference>
            <!--0 to 99 repetitions:-->
            <AdditionalInformation>
              <sequenceNumber>{between1And99999}</sequenceNumber>
              <code>{alphanumeric(5)}</code>
              <!--Optional:-->
              <text>{alphanumeric(1, 70)}</text>
            </AdditionalInformation>
            <!--1 to 999 repetitions:-->
            <ConsignmentItem>
              <goodsItemNumber>{between1And99999}</goodsItemNumber>
              <declarationGoodsItemNumber>{generateDeclarationGoodsNumber()}</declarationGoodsItemNumber>
              <!--Optional:-->
              <declarationType>{alphanumeric(1, 5)}</declarationType>
              <!--Optional:-->
              <countryOfDestination>{alpha(2).toUpperCase}</countryOfDestination>
              <!--Optional:-->
              <Consignee>
                <!--Optional:-->
                <identificationNumber>{alphanumeric(1, 17)}</identificationNumber>
                <!--Optional:-->
                <name>{alphanumeric(1, 70)}</name>
                <!--Optional:-->
                <Address>
                  <streetAndNumber>{alphanumeric(2, 70)}</streetAndNumber>
                  <!--Optional:-->
                  <postcode>{alphanumeric(6, 17)}</postcode>
                  <city>{alphanumeric(3, 35)}</city>
                  <country>{alpha(2).toUpperCase}</country>
                </Address>
              </Consignee>
              <Commodity>
                <descriptionOfGoods>{alphanumeric(3, 512)}</descriptionOfGoods>
                <!--Optional:-->
                <cusCode>{alphanumeric(9)}</cusCode>
                <!--Optional:-->
                <CommodityCode>
                  <harmonizedSystemSubHeadingCode>{alphanumeric(6)}</harmonizedSystemSubHeadingCode>
                  <!--Optional:-->
                  <combinedNomenclatureCode>{alphanumeric(2)}</combinedNomenclatureCode>
                </CommodityCode>
                <!--0 to 99 repetitions:-->
                <DangerousGoods>
                  <sequenceNumber>{between1And99999}</sequenceNumber>
                  <UNNumber>{numeric(4)}</UNNumber>
                </DangerousGoods>
              </Commodity>
              <!--1 to 99 repetitions:-->
              <Packaging>
                <sequenceNumber>{between1And99999}</sequenceNumber>
                <typeOfPackages>{alphanumeric(2)}</typeOfPackages>
                <!--Optional:-->
                <numberOfPackages>{numericNonZeroStart(1, 8)}</numberOfPackages>
                <!--Optional:-->
                <shippingMarks>{numeric(2, 512)}</shippingMarks>
              </Packaging>
              <!--0 to 99 repetitions:-->
              <PreviousDocument>
                <sequenceNumber>{between1And99999}</sequenceNumber>
                <type>{alphanumeric(4)}</type>
                <referenceNumber>{alphanumeric(2, 70)}</referenceNumber>
                <!--Optional:-->
                <goodsItemNumber>{between1And99999}</goodsItemNumber>
                <!--Optional:-->
                <complementOfInformation>{alphanumeric(1, 35)}</complementOfInformation>
              </PreviousDocument>
              <!--0 to 99 repetitions:-->
              <SupportingDocument>
                <sequenceNumber>{between1And99999}</sequenceNumber>
                <type>{alphanumeric(4)}</type>
                <referenceNumber>{alphanumeric(2, 70)}</referenceNumber>
                <!--Optional:-->
                <complementOfInformation>{alphanumeric(2, 35)}</complementOfInformation>
              </SupportingDocument>
              <!--0 to 99 repetitions:-->
              <TransportDocument>
                <sequenceNumber>{between1And99999}</sequenceNumber>
                <type>{alphanumeric(4)}</type>
                <referenceNumber>{alphanumeric(2, 70)}</referenceNumber>
              </TransportDocument>
              <!--0 to 99 repetitions:-->
              <AdditionalReference>
                <sequenceNumber>{between1And99999}</sequenceNumber>
                <type>{alphanumeric(4)}</type>
                <!--Optional:-->
                <referenceNumber>{alphanumeric(2, 70)}</referenceNumber>
              </AdditionalReference>
              <!--0 to 99 repetitions:-->
              <AdditionalInformation>
                <sequenceNumber>{between1And99999}</sequenceNumber>
                <code>{alphanumeric(5)}</code>
                <!--Optional:-->
                <text>{alphanumeric(2, 512)}</text>
              </AdditionalInformation>
            </ConsignmentItem>
          </HouseConsignment>
        </Consignment>
      </ncts:CC043C>
    )

  private def generateIE057Message(correlationId: String): XMLMessage =
    XMLMessage(
      <ncts:CC057C xmlns:ncts="http://ncts.dgtaxud.ec" PhaseID="NCTS5.0">
        <messageSender>{alphanumeric(1, 35)}</messageSender>
        <messageRecipient>{correlationId}</messageRecipient>
        <preparationDateAndTime>{generateLocalDateTime()}</preparationDateAndTime>
        <messageIdentification>{alphanumeric(1, 35)}</messageIdentification>
        <messageType>CC057C</messageType>
        <correlationIdentifier>{alphanumeric(1, 35)}</correlationIdentifier>
        <TransitOperation>
          <MRN>{mrn()}</MRN>
          <businessRejectionType>{alphanumeric(3)}</businessRejectionType>
          <rejectionDateAndTime>{generateLocalDateTime()}</rejectionDateAndTime>
          <rejectionCode>{numeric(2)}</rejectionCode>
          <!--Optional:-->
          <rejectionReason>{alphanumeric(1, 512)}</rejectionReason>
        </TransitOperation>
        <CustomsOfficeOfDestinationActual>
          <referenceNumber>{referenceNumber()}</referenceNumber>
        </CustomsOfficeOfDestinationActual>
        <TraderAtDestination>
          <identificationNumber>{alphanumeric(2, 17)}</identificationNumber>
        </TraderAtDestination>
        <!--0 to 9999 repetitions:-->
        <FunctionalError>
          <errorPointer>{alphanumeric(2, 512)}</errorPointer>
          <errorCode>12</errorCode>
          <errorReason>{alphanumeric(2, 7)}</errorReason>
          <!--Optional:-->
          <originalAttributeValue>{alphanumeric(2, 512)}</originalAttributeValue>
        </FunctionalError>
      </ncts:CC057C>
    )

}
