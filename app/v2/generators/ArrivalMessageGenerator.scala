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
        <messageSender>
          {Strings.alphanumeric(1, 35)}
        </messageSender>
        <messageRecipient>
          {correlationId}
        </messageRecipient>
        <preparationDateAndTime>
          {generateLocalDateTime()}
        </preparationDateAndTime>
        <messageIdentification>
          {Strings.alphanumeric(1, 35)}
        </messageIdentification>
        <messageType>CC928C</messageType>
        <correlationIdentifier>
          {Strings.alphanumeric(1, 35)}
        </correlationIdentifier>
        <TransitOperation>
          <MRN>
            {Strings.mrn()}
          </MRN>

          <!-- TODO -->
          <releaseDate>2014-06-09+01:00</releaseDate>
          <releaseIndicator>token</releaseIndicator>
        </TransitOperation>
        <CustomsOfficeOfDestinationActual>
          <referenceNumber>stringst</referenceNumber>
        </CustomsOfficeOfDestinationActual>
        <TraderAtDestination>
          <identificationNumber>string</identificationNumber>
        </TraderAtDestination>
        <!--Optional:-->
        <Consignment>
          <!--1 to 99 repetitions:-->
          <HouseConsignment>
            <sequenceNumber>token</sequenceNumber>
            <releaseType>token</releaseType>
            <!--0 to 999 repetitions:-->
            <ConsignmentItem>
              <goodsItemNumber>token</goodsItemNumber>
              <declarationGoodsItemNumber>100</declarationGoodsItemNumber>
              <releaseType>token</releaseType>
              <Commodity>
                <descriptionOfGoods>string</descriptionOfGoods>
                <!--Optional:-->
                <cusCode>token</cusCode>
                <!--Optional:-->
                <CommodityCode>
                  <harmonizedSystemSubHeadingCode>token</harmonizedSystemSubHeadingCode>
                  <!--Optional:-->
                  <combinedNomenclatureCode>st</combinedNomenclatureCode>
                </CommodityCode>
                <!--0 to 99 repetitions:-->
                <DangerousGoods>
                  <sequenceNumber>token</sequenceNumber>
                  <UNNumber>token</UNNumber>
                </DangerousGoods>
                <GoodsMeasure>
                  <grossMass>1000.000000000000</grossMass>
                  <!--Optional:-->
                  <netMass>1000.000000000000</netMass>
                </GoodsMeasure>
              </Commodity>
              <!--1 to 99 repetitions:-->
              <Packaging>
                <sequenceNumber>token</sequenceNumber>
                <typeOfPackages>token</typeOfPackages>
                <!--Optional:-->
                <numberOfPackages>100</numberOfPackages>
                <!--Optional:-->
                <shippingMarks>string</shippingMarks>
              </Packaging>
            </ConsignmentItem>
          </HouseConsignment>
        </Consignment>
      </ncts:CC025C>
    )

}
