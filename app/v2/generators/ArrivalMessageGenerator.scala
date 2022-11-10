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
import utils.Strings.alpha
import v2.models.MessageType
import v2.models.MessageType.GoodsReleaseNotification
import v2.models.XMLMessage

import java.time.Clock

@ImplementedBy(classOf[ArrivalMessageGeneratorImpl])
trait ArrivalMessageGenerator extends MessageGenerator

class ArrivalMessageGeneratorImpl @Inject()(clock: Clock) extends Generators with ArrivalMessageGenerator {

  override protected def generateWithCorrelationId(correlationId: String): PartialFunction[MessageType, XMLMessage] = {
    case GoodsReleaseNotification => generateIE025Message(correlationId)
  }

  private def generateIE025Message(correlationId: String): XMLMessage =
    XMLMessage(
      <ncts:CC025C xmlns:ncts="http://ncts.dgtaxud.ec" PhaseID="NCTS5.0">
        <messageSender>{Strings.alphanumeric(1, 35)}</messageSender>
        <messageRecipient>{correlationId}</messageRecipient>
        <preparationDateAndTime>{generateLocalDateTime()}</preparationDateAndTime>
        <messageIdentification>{Strings.alphanumeric(1, 35)}</messageIdentification>
        <messageType>CC928C</messageType>
        <correlationIdentifier>{Strings.alphanumeric(1, 35)}</correlationIdentifier>
        <TransitOperation>
          <MRN>{Strings.mrn()}</MRN>
          <releaseDate>{generateLocalDate()}</releaseDate>
          <releaseIndicator>0</releaseIndicator>
        </TransitOperation>
        <CustomsOfficeOfDestinationActual>
          <referenceNumber>{Strings.referenceNumber()}</referenceNumber>
        </CustomsOfficeOfDestinationActual>
        <TraderAtDestination>
          <identificationNumber>{Strings.alphanumeric(8, 17)}</identificationNumber>
        </TraderAtDestination>
        <!--Optional:-->
        <Consignment>
          <!--1 to 99 repetitions:-->
          <HouseConsignment>
            <sequenceNumber>1</sequenceNumber>
            <releaseType>0</releaseType>
            <!--0 to 999 repetitions:-->
            <ConsignmentItem>
              <goodsItemNumber>{Strings.numeric(1, 5)}</goodsItemNumber>
              <declarationGoodsItemNumber>{generateDeclarationGoodsNumber()}</declarationGoodsItemNumber>
              <releaseType>0</releaseType>
              <Commodity>
                <descriptionOfGoods>{Strings.alphanumeric(64)}</descriptionOfGoods>
                <!--Optional:-->
                <cusCode>{Strings.alphanumeric(9)}</cusCode>
                <!--Optional:-->
                <CommodityCode>
                  <harmonizedSystemSubHeadingCode>{Strings.alphanumeric(6)}</harmonizedSystemSubHeadingCode>
                </CommodityCode>
                <!--0 to 99 repetitions:-->
                <DangerousGoods>
                  <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
                  <UNNumber>{Strings.alphanumeric(4)}</UNNumber>
                </DangerousGoods>
                <GoodsMeasure>
                  <grossMass>1000.00</grossMass>
                  <!--Optional:-->
                  <netMass>1000.00</netMass>
                </GoodsMeasure>
              </Commodity>
              <!--1 to 99 repetitions:-->
              <Packaging>
                <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
                <typeOfPackages>{Strings.alphanumeric(2)}</typeOfPackages>
                <!--Optional:-->
                <numberOfPackages>{Strings.numeric(1, 8)}</numberOfPackages>
                <!--Optional:-->
                <shippingMarks>{Strings.alphanumeric(64)}</shippingMarks>
              </Packaging>
            </ConsignmentItem>
          </HouseConsignment>
        </Consignment>
      </ncts:CC025C>
    )

  private def generateIE043Message(correlationId: String): XMLMessage =
    XMLMessage(
      <ncts:CC043C xmlns:ncts="http://ncts.dgtaxud.ec" PhaseID="NCTS5.0">
        <messageSender>{Strings.alphanumeric(1, 35)}</messageSender>
        <messageRecipient>{correlationId}</messageRecipient>
        <preparationDateAndTime>{generateLocalDateTime()}</preparationDateAndTime>
        <messageIdentification>{Strings.alphanumeric(1, 35)}</messageIdentification>
        <messageType>CC043C</messageType>
        <correlationIdentifier>{Strings.alphanumeric(1, 35)}</correlationIdentifier>
        <TransitOperation>
          <MRN>{Strings.mrn()}</MRN>
          <!--Optional:-->
          <declarationType>{Strings.alphanumeric(5)}</declarationType>
          <!--Optional:-->
          <declarationAcceptanceDate>{generateLocalDate()}</declarationAcceptanceDate>
          <security>{Strings.numeric(1)}</security>
          <reducedDatasetIndicator>{Strings.numeric(1)}</reducedDatasetIndicator>
        </TransitOperation>
        <CustomsOfficeOfDestinationActual>
          <referenceNumber>{Strings.referenceNumber()}</referenceNumber>
        </CustomsOfficeOfDestinationActual>
        <!--Optional:-->
        <HolderOfTheTransitProcedure>
          <!--Optional:-->
          <identificationNumber>{Strings.alphanumeric(8, 17)}</identificationNumber>
          <!--Optional:-->
          <TIRHolderIdentificationNumber>{Strings.alphanumeric(8, 17)}</TIRHolderIdentificationNumber>
          <name>{Strings.alphanumeric(8, 70)}</name>
          <Address>
            <streetAndNumber>{Strings.alphanumeric(8, 70)}</streetAndNumber>
            <!--Optional:-->
            <postcode>{Strings.alphanumeric(6, 17)}</postcode>
            <city>{Strings.alphanumeric(3, 35)}</city>
            <country>{Strings.alpha(2).toUpperCase}</country>
          </Address>
        </HolderOfTheTransitProcedure>
        <TraderAtDestination>
          <identificationNumber>{Strings.alphanumeric(8, 17)}</identificationNumber>
        </TraderAtDestination>
        <!--Optional:-->
        <CTLControl>
          <continueUnloading>{Strings.between1And9}</continueUnloading>
        </CTLControl>
        <!--Optional:-->
        <Consignment>
          <!--Optional:-->
          <countryOfDestination>{Strings.alpha(2)}</countryOfDestination>
          <containerIndicator>{Strings.numeric(1)}</containerIndicator>
          <!--Optional:-->
          <inlandModeOfTransport>{Strings.numeric(1)}</inlandModeOfTransport>
          <!--Optional:-->
          <grossMass>12345.6789</grossMass>
          <!--Optional:-->
          <Consignor>
            <!--Optional:-->
            <identificationNumber>{Strings.alphanumeric(1, 17)}</identificationNumber>
            <!--Optional:-->
            <name>{Strings.alphanumeric(8, 70)}</name>
            <!--Optional:-->
            <Address>
              <streetAndNumber>{Strings.alphanumeric(2, 70)}</streetAndNumber>
              <!--Optional:-->
              <postcode>{Strings.alphanumeric(6, 17)}</postcode>
              <city>{Strings.alphanumeric(3, 35)}</city>
              <country>{Strings.alpha(2).toUpperCase}</country>
            </Address>
          </Consignor>
          <!--Optional:-->
          <Consignee>
            <!--Optional:-->
            <identificationNumber>{Strings.alphanumeric(1, 17)}</identificationNumber>
            <!--Optional:-->
            <name>{Strings.alphanumeric(8, 70)}</name>
            <!--Optional:-->
            <Address>
              <streetAndNumber>{Strings.alphanumeric(8, 70)}</streetAndNumber>
              <!--Optional:-->
              <postcode>{Strings.alphanumeric(6, 17)}</postcode>
              <city>{Strings.alphanumeric(3, 35)}</city>
              <country>{Strings.alpha(2).toUpperCase}</country>
            </Address>
          </Consignee>
          <!--0 to 9999 repetitions:-->
          <TransportEquipment>
            <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
            <!--Optional:-->
            <containerIdentificationNumber>{Strings.alphanumeric(8, 17)}</containerIdentificationNumber>
            <numberOfSeals>{Strings.numeric(0, 4)}</numberOfSeals>
            <!--0 to 99 repetitions:-->
            <Seal>
              <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
              <identifier>{Strings.alphanumeric(8, 20)}</identifier>
            </Seal>
            <!--0 to 9999 repetitions:-->
            <GoodsReference>
              <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
              <declarationGoodsItemNumber>{Strings.numeric(1, 5)}</declarationGoodsItemNumber>
            </GoodsReference>
          </TransportEquipment>
          <!--0 to 999 repetitions:-->
          <DepartureTransportMeans>
            <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
            <typeOfIdentification>{Strings.numeric(2)}</typeOfIdentification>
            <identificationNumber>{Strings.alphanumeric(8, 35)}</identificationNumber>
            <nationality>{Strings.alpha(2).toUpperCase}</nationality>
          </DepartureTransportMeans>
          <!--0 to 9999 repetitions:-->
          <PreviousDocument>
            <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
            <type>{Strings.alphanumeric(4)}</type>
            <referenceNumber>{Strings.alphanumeric(2, 70)}</referenceNumber>
            <!--Optional:-->
            <complementOfInformation>{Strings.alphanumeric(2, 35)}</complementOfInformation>
          </PreviousDocument>
          <!--0 to 99 repetitions:-->
          <SupportingDocument>
            <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
            <type>{Strings.alphanumeric(4)}</type>
            <referenceNumber>{Strings.alphanumeric(8, 70)}</referenceNumber>
            <!--Optional:-->
            <complementOfInformation>{Strings.alphanumeric(8, 35)}</complementOfInformation>
          </SupportingDocument>
          <!--0 to 99 repetitions:-->
          <TransportDocument>
            <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
            <type>{Strings.alphanumeric(4)}</type>
            <referenceNumber>{Strings.alphanumeric(8, 70)}</referenceNumber>
          </TransportDocument>
          <!--0 to 99 repetitions:-->
          <AdditionalReference>
            <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
            <type>{Strings.alphanumeric(4)}</type>
            <!--Optional:-->
            <referenceNumber>{Strings.alphanumeric(8, 70)}</referenceNumber>
          </AdditionalReference>
          <!--0 to 99 repetitions:-->
          <AdditionalInformation>
            <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
            <code>{Strings.alphanumeric(1, 5)}</code>
            <!--Optional:-->
            <text>{Strings.alphanumeric(20, 512)}</text>
          </AdditionalInformation>
          <!--0 to 9 repetitions:-->
          <Incident>
            <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
            <code>{Strings.numeric(1)}</code>
            <text>{Strings.alphanumeric(20, 512)}</text>
            <!--Optional:-->
            <Endorsement>
              <date>{generateLocalDate()}</date>
              <authority>{Strings.alphanumeric(20, 35)}</authority>
              <place>{Strings.alphanumeric(20, 35)}</place>
              <country>{Strings.alpha(2).toUpperCase}</country>
            </Endorsement>
            <Location>
              <qualifierOfIdentification>{Strings.alpha(1).toUpperCase}</qualifierOfIdentification>
              <!--Optional:-->
              <UNLocode>{Strings.alphanumeric(5, 17)}</UNLocode>
              <country>{Strings.alpha(2).toUpperCase}</country>
              <!--Optional:-->
              <GNSS>
                <latitude>{Strings.alphanumeric(5, 17)}</latitude>
                <longitude>{Strings.alphanumeric(5, 17)}</longitude>
              </GNSS>
              <!--Optional:-->
              <Address>
                <streetAndNumber>{Strings.alphanumeric(8, 70)}</streetAndNumber>
                <!--Optional:-->
                <postcode>{Strings.alphanumeric(6, 17)}</postcode>
                <city>{Strings.alphanumeric(3, 35)}</city>
              </Address>
            </Location>
            <!--0 to 9999 repetitions:-->
            <TransportEquipment>
              <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
              <!--Optional:-->
              <containerIdentificationNumber>{Strings.alphanumeric(8, 17)}</containerIdentificationNumber>
              <!--Optional:-->
              <numberOfSeals>{Strings.numeric(1, 4)}</numberOfSeals>
              <!--0 to 99 repetitions:-->
              <Seal>
                <sequenceNumber>{Strings.numeric(1, 4)}</sequenceNumber>
                <identifier>{Strings.alphanumeric(5, 20)}</identifier>
              </Seal>
              <!--0 to 9999 repetitions:-->
              <GoodsReference>
                <sequenceNumber>{Strings.numeric(1, 4)}</sequenceNumber>
                <declarationGoodsItemNumber>{Strings.numeric(1, 5)}</declarationGoodsItemNumber>
              </GoodsReference>
            </TransportEquipment>
            <!--Optional:-->
            <Transhipment>
              <containerIndicator>{Strings.numeric(1)}</containerIndicator>
              <TransportMeans>
                <typeOfIdentification>{Strings.numeric(2)}</typeOfIdentification>
                <identificationNumber>{Strings.alphanumeric(8, 35)}</identificationNumber>
                <nationality>{Strings.alpha(2).toUpperCase}</nationality>
              </TransportMeans>
            </Transhipment>
          </Incident>
          <!--1 to 99 repetitions:-->
          <HouseConsignment>
            <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
            <grossMass>9876.54321</grossMass>
            <!--Optional:-->
            <securityIndicatorFromExportDeclaration>{Strings.numeric(1)}</securityIndicatorFromExportDeclaration>
            <!--Optional:-->
            <Consignor>
              <!--Optional:-->
              <identificationNumber>{Strings.alphanumeric(8, 17)}</identificationNumber>
              <!--Optional:-->
              <name>{Strings.alphanumeric(4, 70)}</name>
              <!--Optional:-->
              <Address>
                <streetAndNumber>{Strings.alphanumeric(8, 70)}</streetAndNumber>
                <!--Optional:-->
                <postcode>{Strings.alphanumeric(3, 17)}</postcode>
                <city>{Strings.alphanumeric(3, 35)}</city>
                <country>{Strings.alpha(2).toUpperCase}</country>
              </Address>
            </Consignor>
            <!--Optional:-->
            <Consignee>
              <!--Optional:-->
              <identificationNumber>{Strings.alphanumeric(1, 17)}</identificationNumber>
              <!--Optional:-->
              <name>{Strings.alphanumeric(2, 70)}</name>
              <!--Optional:-->
              <Address>
                <streetAndNumber>{Strings.alphanumeric(8, 70)}</streetAndNumber>
                <!--Optional:-->
                <postcode>{Strings.alphanumeric(3, 17)}</postcode>
                <city>{Strings.alphanumeric(3, 35)}</city>
                <country>{Strings.alpha(2).toUpperCase}</country>
              </Address>
            </Consignee>
            <!--0 to 999 repetitions:-->
            <DepartureTransportMeans>
              <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
              <typeOfIdentification>{Strings.numeric(2)}</typeOfIdentification>
              <identificationNumber>{Strings.alphanumeric(4, 35)}</identificationNumber>
              <nationality>{Strings.alpha(2).toUpperCase}</nationality>
            </DepartureTransportMeans>
            <!--0 to 99 repetitions:-->
            <PreviousDocument>
              <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
              <type>{Strings.alphanumeric(4)}</type>
              <referenceNumber>{Strings.alphanumeric(4, 70)}</referenceNumber>
              <!--Optional:-->
              <complementOfInformation>{Strings.alphanumeric(1, 35)}</complementOfInformation>
            </PreviousDocument>
            <!--0 to 99 repetitions:-->
            <SupportingDocument>
              <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
              <type>{Strings.alphanumeric(4)}</type>
              <referenceNumber>{Strings.alphanumeric(1, 70)}</referenceNumber>
              <!--Optional:-->
              <complementOfInformation>{Strings.alphanumeric(1, 35)}</complementOfInformation>
            </SupportingDocument>
            <!--0 to 99 repetitions:-->
            <TransportDocument>
              <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
              <type>{Strings.alphanumeric(4)}</type>
              <referenceNumber>{Strings.alphanumeric(1, 70)}</referenceNumber>
            </TransportDocument>
            <!--0 to 99 repetitions:-->
            <AdditionalReference>
              <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
              <type>{Strings.alphanumeric(4)}</type>
              <!--Optional:-->
              <referenceNumber>{Strings.alphanumeric(1, 70)}</referenceNumber>
            </AdditionalReference>
            <!--0 to 99 repetitions:-->
            <AdditionalInformation>
              <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
              <code>{Strings.alphanumeric(4)}</code>
              <!--Optional:-->
              <text>{Strings.alphanumeric(1, 70)}</text>
            </AdditionalInformation>
            <!--1 to 999 repetitions:-->
            <ConsignmentItem>
              <goodsItemNumber>{Strings.numeric(1, 5)}</goodsItemNumber>
              <declarationGoodsItemNumber>{Strings.numeric(1, 5)}</declarationGoodsItemNumber>
              <!--Optional:-->
              <declarationType>{Strings.alphanumeric(1, 5)}</declarationType>
              <!--Optional:-->
              <countryOfDestination>{Strings.alpha(2).toUpperCase}</countryOfDestination>
              <!--Optional:-->
              <Consignee>
                <!--Optional:-->
                <identificationNumber>{Strings.alphanumeric(1, 17)}</identificationNumber>
                <!--Optional:-->
                <name>{Strings.alphanumeric(1, 70)}</name>
                <!--Optional:-->
                <Address>
                  <streetAndNumber>{Strings.alphanumeric(2, 70)}</streetAndNumber>
                  <!--Optional:-->
                  <postcode>{Strings.alphanumeric(6, 17)}</postcode>
                  <city>{Strings.alphanumeric(3, 35)}</city>
                  <country>{Strings.alpha(2).toUpperCase}</country>
                </Address>
              </Consignee>
              <Commodity>
                <descriptionOfGoods>{Strings.alphanumeric(3, 512)}</descriptionOfGoods>
                <!--Optional:-->
                <cusCode>{Strings.alphanumeric(1, 9)}</cusCode>
                <!--Optional:-->
                <CommodityCode>
                  <harmonizedSystemSubHeadingCode>{Strings.alphanumeric(1, 6)}</harmonizedSystemSubHeadingCode>
                  <!--Optional:-->
                  <combinedNomenclatureCode>{Strings.alphanumeric(1, 2)}</combinedNomenclatureCode>
                </CommodityCode>
                <!--0 to 99 repetitions:-->
                <DangerousGoods>
                  <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
                  <UNNumber>{Strings.numeric(1, 4)}</UNNumber>
                </DangerousGoods>
                <GoodsMeasure>
                  <grossMass>2468.1234</grossMass>
                  <!--Optional:-->
                  <netMass>12345.78900</netMass>
                </GoodsMeasure>
              </Commodity>
              <!--1 to 99 repetitions:-->
              <Packaging>
                <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
                <typeOfPackages>{Strings.alphanumeric(2)}</typeOfPackages>
                <!--Optional:-->
                <numberOfPackages>{Strings.numeric(1, 8)}</numberOfPackages>
                <!--Optional:-->
                <shippingMarks>{Strings.numeric(2, 512)}</shippingMarks>
              </Packaging>
              <!--0 to 99 repetitions:-->
              <PreviousDocument>
                <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
                <type>{Strings.alphanumeric(4)}</type>
                <referenceNumber>{Strings.alphanumeric(2, 70)}</referenceNumber>
                <!--Optional:-->
                <goodsItemNumber>{Strings.numeric(1, 5)}</goodsItemNumber>
                <!--Optional:-->
                <complementOfInformation>{Strings.alphanumeric(1, 35)}</complementOfInformation>
              </PreviousDocument>
              <!--0 to 99 repetitions:-->
              <SupportingDocument>
                <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
                <type>{Strings.alphanumeric(4)}</type>
                <referenceNumber>{Strings.alphanumeric(2, 70)}</referenceNumber>
                <!--Optional:-->
                <complementOfInformation>{Strings.alphanumeric(2, 35)}</complementOfInformation>
              </SupportingDocument>
              <!--0 to 99 repetitions:-->
              <TransportDocument>
                <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
                <type>{Strings.alphanumeric(4)}</type>
                <referenceNumber>{Strings.alphanumeric(2, 70)}</referenceNumber>
              </TransportDocument>
              <!--0 to 99 repetitions:-->
              <AdditionalReference>
                <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
                <type>{Strings.alphanumeric(4)}</type>
                <!--Optional:-->
                <referenceNumber>{Strings.alphanumeric(2, 70)}</referenceNumber>
              </AdditionalReference>
              <!--0 to 99 repetitions:-->
              <AdditionalInformation>
                <sequenceNumber>{Strings.numeric(1, 5)}</sequenceNumber>
                <code>{Strings.alphanumeric(5)}</code>
                <!--Optional:-->
                <text>{Strings.alphanumeric(2, 512)}</text>
              </AdditionalInformation>
            </ConsignmentItem>
          </HouseConsignment>
        </Consignment>
        
      </ncts:CC043C>
    )

}
