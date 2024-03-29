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
import models.MessageType.ArrivalNegativeAcknowledgement
import models.MessageType.CancellationDecision
import models.MessageType.ControlDecisionNotification
import models.MessageType.DeclarationRejected
import models.MessageType.GuaranteeNotValid
import models.MessageType.MrnAllocated
import models.MessageType.NoReleaseForTransit
import models.MessageType.PositiveAcknowledgement
import models.MessageType.ReleaseForTransit
import models.MessageType.WriteOffNotification
import models.MessageType.XMLSubmissionNegativeAcknowledgement
import utils.Strings

import java.time.Clock
import scala.xml.NodeSeq

class DepartureMessageGenerator @Inject() (clock: Clock) extends Generator(clock) {

  val guaranteeRef = s"${Strings.numeric(2)}GB${Strings.alphanumeric(13)}"

  def generate(): PartialFunction[MessageType, NodeSeq] = {
    case PositiveAcknowledgement              => generateIE928Message()
    case NoReleaseForTransit                  => generateIE051Message()
    case ReleaseForTransit                    => generateIE029Message()
    case ControlDecisionNotification          => generateIE060Message()
    case MrnAllocated                         => generateIE028Message()
    case DeclarationRejected                  => generateIE016Message()
    case CancellationDecision                 => generateIE009Message()
    case WriteOffNotification                 => generateIE045Message()
    case GuaranteeNotValid                    => generateIE055Message()
    case XMLSubmissionNegativeAcknowledgement => generateIE917Message()
    case ArrivalNegativeAcknowledgement       => generateIE917Message()
  }

  private def generateIE928Message(): NodeSeq =
    <CC928A>
      <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
      <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
      <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
      <SenIdeCodQuaMES4>{Strings.alphanumeric(1, 4)}</SenIdeCodQuaMES4>
      <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
      <RecIdeCodQuaMES7>{Strings.alphanumeric(1, 4)}</RecIdeCodQuaMES7>
      <DatOfPreMES9>{localDate}</DatOfPreMES9>
      <TimOfPreMES10>{localTime}</TimOfPreMES10>
      <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
      <RecRefMES12>{Strings.alphanumeric(1, 14)}</RecRefMES12>
      <RecRefQuaMES13>{Strings.alphanumeric(2)}</RecRefQuaMES13>
      <AppRefMES14>{Strings.alphanumeric(1, 14)}</AppRefMES14>
      <PriMES15>{Strings.alpha(1)}</PriMES15>
      <AckReqMES16>{Strings.numeric(1)}</AckReqMES16>
      <ComAgrIdMES17>{Strings.alphanumeric(1, 35)}</ComAgrIdMES17>
      <TesIndMES18>{Strings.numeric(1)}</TesIndMES18>
      <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
      <MesTypMES20>{Strings.alphanumeric(1, 6)}</MesTypMES20>
      <ComAccRefMES21>{Strings.alphanumeric(1, 35)}</ComAccRefMES21>
      <MesSeqNumMES22>{Strings.numeric(1, 2)}</MesSeqNumMES22>
      <FirAndLasTraMES23>{Strings.alpha(1)}</FirAndLasTraMES23>
      <HEAHEA>
        <RefNumHEA4>{Strings.alphanumeric(1, 22)}</RefNumHEA4>
      </HEAHEA>
      <TRAPRIPC1>
      </TRAPRIPC1>
      <CUSOFFDEPEPT>
        <RefNumEPT1>{Strings.alphanumeric(8)}</RefNumEPT1>
      </CUSOFFDEPEPT>
    </CC928A>

  private def generateIE051Message(): NodeSeq =
    <CC051B>
      <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
      <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
      <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
      <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
      <DatOfPreMES9>{localDate}</DatOfPreMES9>
      <TimOfPreMES10>{localTime}</TimOfPreMES10>
      <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
      <AppRefMES14>NCTS</AppRefMES14>
      <TesIndMES18>0</TesIndMES18>
      <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
      <MesTypMES20>GB051B</MesTypMES20>
      <HEAHEA>
        <RefNumHEA4>{Strings.alphanumeric(1, 22)}</RefNumHEA4>
        <DocNumHEA5>{Strings.alphanumeric(1, 21)}</DocNumHEA5>
        <TypOfDecHEA24>T1</TypOfDecHEA24>
        <CouOfDesCodHEA30>IT</CouOfDesCodHEA30>
        <CouOfDisCodHEA55>GB</CouOfDisCodHEA55>
        <IdeOfMeaOfTraAtDHEA78>NC15 REG</IdeOfMeaOfTraAtDHEA78>
        <ConIndHEA96>0</ConIndHEA96>
        <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
        <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
        <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
        <TotNumOfPacHEA306>10</TotNumOfPacHEA306>
        <TotGroMasHEA307>1000</TotGroMasHEA307>
        <DecDatHEA383>{localDate}</DecDatHEA383>
        <DecPlaHEA394>Dover</DecPlaHEA394>
        <NoRelMotHEA272>Test</NoRelMotHEA272>
      </HEAHEA>
      <TRAPRIPC1>
        <NamPC17>NCTS UK TEST LAB HMCE</NamPC17>
        <StrAndNumPC122>11TH FLOOR, ALEX HOUSE, VICTORIA AV</StrAndNumPC122>
        <PosCodPC123>SS99 1AA</PosCodPC123>
        <CitPC124>SOUTHEND-ON-SEA, ESSEX</CitPC124>
        <CouPC125>GB</CouPC125>
        <TINPC159>GB954131533000</TINPC159>
      </TRAPRIPC1>
      <TRACONCO1>
        <NamCO17>NCTS UK TEST LAB HMCE</NamCO17>
        <StrAndNumCO122>11TH FLOOR, ALEX HOUSE, VICTORIA AV</StrAndNumCO122>
        <PosCodCO123>SS99 1AA</PosCodCO123>
        <CitCO124>SOUTHEND-ON-SEA, ESSEX</CitCO124>
        <CouCO125>GB</CouCO125>
        <TINCO159>GB954131533000</TINCO159>
      </TRACONCO1>
      <TRACONCE1>
        <NamCE17>NCTS UK TEST LAB HMCE</NamCE17>
        <StrAndNumCE122>ITALIAN OFFICE</StrAndNumCE122>
        <PosCodCE123>IT99 1IT</PosCodCE123>
        <CitCE124>MILAN</CitCE124>
        <CouCE125>IT</CouCE125>
        <TINCE159>IT11ITALIANC11</TINCE159>
      </TRACONCE1>
      <CUSOFFDEPEPT>
        <RefNumEPT1>GB000060</RefNumEPT1>
      </CUSOFFDEPEPT>
      <CUSOFFTRARNS>
        <RefNumRNS1>FR001260</RefNumRNS1>
        <ArrTimTRACUS085>{localDateTime}</ArrTimTRACUS085>
      </CUSOFFTRARNS>
      <CUSOFFDESEST>
        <RefNumEST1>IT018105</RefNumEST1>
      </CUSOFFDESEST>
      <CONRESERS>
        <ConDatERS14>{localDate}</ConDatERS14>
        <ConResCodERS16>B1</ConResCodERS16>
      </CONRESERS>
      <RESOFCON534>
        <DesTOC2>See Header for details</DesTOC2>
        <ConInd424>OT</ConInd424>
      </RESOFCON534>
      <GUAGUA>
        <GuaTypGUA1>1</GuaTypGUA1>
        <GUAREFREF>
          <GuaRefNumGRNREF1>{guaranteeRef}</GuaRefNumGRNREF1>
          <AccCodREF6>AC01</AccCodREF6>
          <VALLIMECVLE>
            <NotValForECVLE1>0</NotValForECVLE1>
          </VALLIMECVLE>
        </GUAREFREF>
      </GUAGUA>
      <GOOITEGDS>
        <IteNumGDS7>1</IteNumGDS7>
        <GooDesGDS23>Daffodils</GooDesGDS23>
        <GroMasGDS46>1000</GroMasGDS46>
        <NetMasGDS48>950</NetMasGDS48>
        <RESOFCONROC><ConIndROC1>OR</ConIndROC1>
        </RESOFCONROC>
        <PACGS2>
          <MarNumOfPacGS21>AB234</MarNumOfPacGS21>
          <KinOfPacGS23>BX</KinOfPacGS23>
          <NumOfPacGS24>10</NumOfPacGS24>
        </PACGS2>
      </GOOITEGDS>
    </CC051B>

  private def generateIE029Message(): NodeSeq = {
    <CC029B>
      <SynIdeMES1>UNOC</SynIdeMES1>
      <SynVerNumMES2>3</SynVerNumMES2>
      <MesSenMES3>NTA.GB</MesSenMES3>
      <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
      <DatOfPreMES9>{localDate}</DatOfPreMES9>
      <TimOfPreMES10>{localTime}</TimOfPreMES10>
      <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
      <AppRefMES14>NCTS</AppRefMES14>
      <TesIndMES18>0</TesIndMES18>
      <MesIdeMES19>{Strings.alphanumeric(1, 6)}</MesIdeMES19>
      <MesTypMES20>GB029B</MesTypMES20>
      <HEAHEA>
        <RefNumHEA4>{Strings.alphanumeric(1, 22)}</RefNumHEA4>
        <DocNumHEA5>{Strings.alphanumeric(1, 21)}</DocNumHEA5>
        <TypOfDecHEA24>T1</TypOfDecHEA24>
        <CouOfDesCodHEA30>IT</CouOfDesCodHEA30>
        <CouOfDisCodHEA55>GB</CouOfDisCodHEA55>
        <IdeOfMeaOfTraAtDHEA78>NC15 REG</IdeOfMeaOfTraAtDHEA78>
        <NatOfMeaOfTraAtDHEA80>GB</NatOfMeaOfTraAtDHEA80>
        <ConIndHEA96>0</ConIndHEA96>
        <NCTRetCopHEA104>0</NCTRetCopHEA104>
        <AccDatHEA158>20201028</AccDatHEA158>
        <IssDatHEA186>20201028</IssDatHEA186>
        <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
        <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
        <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
        <TotNumOfPacHEA306>10</TotNumOfPacHEA306>
        <TotGroMasHEA307>1000</TotGroMasHEA307>
        <BinItiHEA246>0</BinItiHEA246>
        <AutIdHEA380>GB-AUTH-42</AutIdHEA380>
        <DecDatHEA383>{localDate}</DecDatHEA383>
        <DecPlaHEA394>Dover</DecPlaHEA394>
      </HEAHEA>
      <TRAPRIPC1>
        <NamPC17>NCTS UK TEST LAB HMCE</NamPC17>
        <StrAndNumPC122>11TH FLOOR, ALEX HOUSE, VICTORIA AV</StrAndNumPC122>
        <PosCodPC123>SS99 1AA</PosCodPC123>
        <CitPC124>SOUTHEND-ON-SEA, ESSEX</CitPC124>
        <CouPC125>GB</CouPC125>
        <TINPC159>GB954131533000</TINPC159>
      </TRAPRIPC1>
      <TRACONCO1>
        <NamCO17>NCTS UK TEST LAB HMCE</NamCO17>
        <StrAndNumCO122>11TH FLOOR, ALEX HOUSE, VICTORIA AV</StrAndNumCO122>
        <PosCodCO123>SS99 1AA</PosCodCO123>
        <CitCO124>SOUTHEND-ON-SEA, ESSEX</CitCO124>
        <CouCO125>GB</CouCO125>
        <TINCO159>GB954131533000</TINCO159>
      </TRACONCO1>
      <TRACONCE1>
        <NamCE17>NCTS UK TEST LAB HMCE</NamCE17>
        <StrAndNumCE122>ITALIAN OFFICE</StrAndNumCE122>
        <PosCodCE123>IT99 1IT</PosCodCE123>
        <CitCE124>MILAN</CitCE124>
        <CouCE125>IT</CouCE125>
        <TINCE159>IT11ITALIANC11</TINCE159>
      </TRACONCE1>
      <CUSOFFDEPEPT>
        <RefNumEPT1>GB000060</RefNumEPT1>
      </CUSOFFDEPEPT>
      <CUSOFFTRARNS>
        <RefNumRNS1>FR001260</RefNumRNS1>
        <ArrTimTRACUS085>{localDateTime}</ArrTimTRACUS085>
      </CUSOFFTRARNS>
      <CUSOFFDESEST>
        <RefNumEST1>IT018100</RefNumEST1>
      </CUSOFFDESEST>
      <CUSOFFRETCOPOCP>
        <RefNumOCP1>GB000001</RefNumOCP1>
        <CusOffNamOCP2>Central Community Transit Office</CusOffNamOCP2>
        <StrAndNumOCP3>BT-CCTO, HM Revenue and Customs</StrAndNumOCP3>
        <CouOCP4>GB</CouOCP4>
        <PosCodOCP6>BX9 1EH</PosCodOCP6>
        <CitOCP7>SALFORD</CitOCP7>
      </CUSOFFRETCOPOCP>
      <CONRESERS>
        <ConDatERS14>{localDate}</ConDatERS14>
        <ConResCodERS16>A3</ConResCodERS16>
        <ConByERS18>Not Controlled</ConByERS18>
        <DatLimERS69>{localDateAdjusted(30)}</DatLimERS69>
      </CONRESERS>
      <SEAINFSLI>
        <SeaNumSLI2>1</SeaNumSLI2>
        <SEAIDSID>
          <SeaIdeSID1>NCTS001</SeaIdeSID1>
        </SEAIDSID>
      </SEAINFSLI>
      <GUAGUA>
        <GuaTypGUA1>1</GuaTypGUA1>
        <GUAREFREF>
          <GuaRefNumGRNREF1>{guaranteeRef}</GuaRefNumGRNREF1>
          <AccCodREF6>AC01</AccCodREF6>
          <VALLIMECVLE>
            <NotValForECVLE1>0</NotValForECVLE1>
          </VALLIMECVLE>
        </GUAREFREF>
      </GUAGUA>
      <GOOITEGDS>
        <IteNumGDS7>1</IteNumGDS7>
        <GooDesGDS23>Daffodils</GooDesGDS23>
        <GroMasGDS46>1000</GroMasGDS46>
        <NetMasGDS48>950</NetMasGDS48>
        <SPEMENMT2>
          <AddInfMT21>20.22EUR{guaranteeRef}</AddInfMT21>
          <AddInfCodMT23>CAL</AddInfCodMT23>
        </SPEMENMT2>
        <PACGS2>
          <MarNumOfPacGS21>AB234</MarNumOfPacGS21>
          <KinOfPacGS23>BX</KinOfPacGS23>
          <NumOfPacGS24>10</NumOfPacGS24>
        </PACGS2>
      </GOOITEGDS>
    </CC029B>
  }

  private def generateIE060Message(): NodeSeq =
    <CC060A>
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
        <DatOfConNotHEA148>{Strings.numeric8()}</DatOfConNotHEA148>
      </HEAHEA>
      <TRAPRIPC1>
        <NamPC17>{Strings.alphanumeric(1, 35)}</NamPC17>
        <StrAndNumPC122>{Strings.alphanumeric(1, 35)}</StrAndNumPC122>
        <PosCodPC123>{Strings.alphanumeric(1, 9)}</PosCodPC123>
        <CitPC124>{Strings.alphanumeric(1, 35)}</CitPC124>
        <CouPC125>{Strings.alpha(2)}</CouPC125>
      </TRAPRIPC1>
      <CUSOFFDEPEPT>
        <RefNumEPT1>{Strings.alphanumeric(8)}</RefNumEPT1>
      </CUSOFFDEPEPT>
    </CC060A>

  private def generateIE028Message(): NodeSeq =
    <CC028A>
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
        <RefNumHEA4>{Strings.alphanumeric(1, 22)}</RefNumHEA4>
        <DocNumHEA5>{Strings.alphanumeric(1, 21)}</DocNumHEA5>
        <AccDatHEA158>{Strings.numeric8()}</AccDatHEA158>
      </HEAHEA>
      <TRAPRIPC1/>
      <CUSOFFDEPEPT>
        <RefNumEPT1>{Strings.alphanumeric(8)}</RefNumEPT1>
      </CUSOFFDEPEPT>
    </CC028A>

  private def generateIE016Message(): NodeSeq =
    <CC016A>
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
        <RefNumHEA4>{Strings.alphanumeric(1, 22)}</RefNumHEA4>
        <TypOfDecHEA24>{Strings.alphanumeric(1, 9)}</TypOfDecHEA24>
        <DecRejDatHEA159>{Strings.numeric8()}</DecRejDatHEA159>
      </HEAHEA>
      <FUNERRER1>
        <ErrTypER11>{Strings.numeric(2)}</ErrTypER11>
        <ErrPoiER12>{Strings.alphanumeric(1, 35)}</ErrPoiER12>
      </FUNERRER1>
    </CC016A>

  private def generateIE009Message(): NodeSeq =
    <CC009A>
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
        <CanDecHEA93>1</CanDecHEA93>
        <DatOfCanReqHEA147>{localDate}</DatOfCanReqHEA147>
        <CanIniByCusHEA94>1</CanIniByCusHEA94>
        <DatOfCanDecHEA146>{localDate}</DatOfCanDecHEA146>
      </HEAHEA>
      <TRAPRIPC1/>
      <CUSOFFDEPEPT>
        <RefNumEPT1>{Strings.alphanumeric(8)}</RefNumEPT1>
      </CUSOFFDEPEPT>
    </CC009A>

  private def generateIE045Message(): NodeSeq =
    <CC045A>
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
        <WriOffDatHEA619>{Strings.numeric8()}</WriOffDatHEA619>
      </HEAHEA>
      <TRAPRIPC1>
        <NamPC17>{Strings.alphanumeric(1, 35)}</NamPC17>
        <StrAndNumPC122>{Strings.alphanumeric(1, 35)}</StrAndNumPC122>
        <PosCodPC123>{Strings.alphanumeric(1, 9)}</PosCodPC123>
        <CitPC124>{Strings.alphanumeric(1, 35)}</CitPC124>
        <CouPC125>{Strings.alpha(2)}</CouPC125>
      </TRAPRIPC1>
      <CUSOFFDEPEPT>
        <RefNumEPT1>{Strings.alphanumeric(8)}</RefNumEPT1>
      </CUSOFFDEPEPT>
    </CC045A>

  private def generateIE055Message(): NodeSeq =
    <CC055A>
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
      </HEAHEA>
      <TRAPRIPC1/>
      <CUSOFFDEPEPT>
        <RefNumEPT1>{Strings.alphanumeric(8)}</RefNumEPT1>
      </CUSOFFDEPEPT>
      <GUAREF2>
        <GuaRefNumGRNREF21>{Strings.alphanumeric(1, 24)}</GuaRefNumGRNREF21>
        <INVGUARNS>
          <InvGuaReaCodRNS11>{Strings.alphanumeric(1, 3)}</InvGuaReaCodRNS11>
          <InvGuaReaRNS12>{Strings.alphanumeric(1, 10)}</InvGuaReaRNS12>
        </INVGUARNS>
      </GUAREF2>
    </CC055A>

  private def generateIE917Message(): NodeSeq =
    <CC917A><SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
      <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
      <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
      <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
      <DatOfPreMES9>{localDate}</DatOfPreMES9>
      <TimOfPreMES10>{localTime}</TimOfPreMES10>
      <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
      <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
      <MesTypMES20>{Strings.alphanumeric(1, 6)}</MesTypMES20>
      <HEAHEA>
        <OriMesIdeMES22>{Strings.alphanumeric(1, 14)}</OriMesIdeMES22>
        <DocNumHEA5>{Strings.alphanumeric(1, 21)}</DocNumHEA5>
        <RefNumHEA4>{Strings.alphanumeric(1, 22)}</RefNumHEA4>
      </HEAHEA>
      <FUNERRER1><ErrTypER11>{Strings.numeric(2)}</ErrTypER11>
        <ErrPoiER12>{Strings.alphanumeric(1, 35)}</ErrPoiER12>
        <ErrReaER13>{Strings.alphanumeric(1, 35)}</ErrReaER13>
      </FUNERRER1>
    </CC917A>

}
