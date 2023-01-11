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

package generators

import com.google.inject.Inject
import models.MessageType
import models.MessageType.ArrivalRejection
import models.MessageType.GoodsReleased
import models.MessageType.UnloadingRemarksRejection
import utils.Strings

import java.time.Clock
import scala.xml.NodeSeq

class ArrivalMessageGenerator @Inject()(clock: Clock) extends Generator(clock) {

  def generate(): PartialFunction[MessageType, NodeSeq] = {
    case ArrivalRejection          => generateIE008Message()
    case UnloadingRemarksRejection => generateIE058Message()
    case GoodsReleased             => generateIE025Message()
  }

  private def generateIE008Message(): NodeSeq =
    <CC008A>
      <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
      <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
      <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
      <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
      <DatOfPreMES9>{localDate}</DatOfPreMES9>
      <TimOfPreMES10>{localTime}</TimOfPreMES10>
      <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
      <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
      <MesTypMES20>{Strings.alphanumeric(1, 6)}</MesTypMES20>
      <HEAHEA>
        <DocNumHEA5>{Strings.alphanumeric(1, 21)}</DocNumHEA5>
        <ArrRejDatHEA142>{Strings.numeric8()}</ArrRejDatHEA142>
      </HEAHEA>
    </CC008A>

  private def generateIE025Message(): NodeSeq =
    <CC025A>
      <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
      <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
      <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
      <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
      <DatOfPreMES9>{localDate}</DatOfPreMES9>
      <TimOfPreMES10>{localTime}</TimOfPreMES10>
      <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
      <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
      <MesTypMES20>{Strings.alphanumeric(1, 6)}</MesTypMES20>
      <HEAHEA>
        <DocNumHEA5>{Strings.alphanumeric(1, 21)}</DocNumHEA5>
        <GooRelDatHEA176>{Strings.numeric8()}</GooRelDatHEA176>
      </HEAHEA>
      <TRADESTRD/>
      <CUSOFFPREOFFRES>
        <RefNumRES1>{Strings.alphanumeric(8)}</RefNumRES1>
      </CUSOFFPREOFFRES>
    </CC025A>

  private def generateIE058Message(): NodeSeq =
    <CC058A>
      <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
      <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
      <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
      <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
      <DatOfPreMES9>{localDate}</DatOfPreMES9>
      <TimOfPreMES10>{localTime}</TimOfPreMES10>
      <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
      <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
      <MesTypMES20>{Strings.alphanumeric(1, 6)}</MesTypMES20>
      <HEAHEA>
        <DocNumHEA5>21GB00014210026352</DocNumHEA5>
        <UnlRemRejDatHEA218>{Strings.numeric8()}</UnlRemRejDatHEA218>
        <UnlRemRejReaHEA280>The IE044 Message was invalid</UnlRemRejReaHEA280>
      </HEAHEA>
      <FUNERRER1>
        <ErrTypER11>12</ErrTypER11>
        <ErrPoiER12>REM.Unloading Date</ErrPoiER12>
        <OriAttValER14>20190101</OriAttValER14>
      </FUNERRER1>
      <FUNERRER1>
        <ErrTypER11>12</ErrTypER11>
        <ErrPoiER12>HEA.Total number of items</ErrPoiER12>
        <OriAttValER14>1000</OriAttValER14>
      </FUNERRER1>
      <FUNERRER1>
        <ErrTypER11>12</ErrTypER11>
        <ErrPoiER12>HEA.Total gross mass</ErrPoiER12>
        <OriAttValER14>1000.123</OriAttValER14>
      </FUNERRER1>
      <FUNERRER1>
        <ErrTypER11>12</ErrTypER11>
        <ErrPoiER12>HEA.Identity of means of transport at departure (exp/trans)</ErrPoiER12>
        <OriAttValER14>RegNumber</OriAttValER14>
      </FUNERRER1>
      <FUNERRER1>
        <ErrTypER11>12</ErrTypER11>
        <ErrPoiER12>HEA.Total number of packages</ErrPoiER12>
        <OriAttValER14>12345</OriAttValER14>
      </FUNERRER1>
      <FUNERRER1>
        <ErrTypER11>12</ErrTypER11>
        <ErrPoiER12>TRD.Country</ErrPoiER12>
        <OriAttValER14>GB</OriAttValER14>
      </FUNERRER1>
      <FUNERRER1>
        <ErrTypER11>15</ErrTypER11>
        <ErrPoiER12>SLI.Seals number</ErrPoiER12>
        <ErrReaER13>R206</ErrReaER13>
      </FUNERRER1>
      <FUNERRER1>
        <ErrTypER11>13</ErrTypER11>
        <ErrPoiER12>GDS(1).ROC</ErrPoiER12>
      </FUNERRER1>
    </CC058A>

}
