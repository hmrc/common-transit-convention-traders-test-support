/*
 * Copyright 2021 HM Revenue & Customs
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

import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import models.MessageType.ArrivalRejection
import models.MessageType.CancellationDecision
import models.MessageType.ControlDecisionNotification
import models.MessageType.DeclarationRejected
import models.MessageType.GoodsReleased
import models.MessageType.GuaranteeNotValid
import models.MessageType.MrnAllocated
import models.MessageType.NoReleaseForTransit
import models.MessageType.PositiveAcknowledgement
import models.MessageType.ReleaseForTransit
import models.MessageType.UnloadingPermission
import models.MessageType.UnloadingRemarksRejection
import models.MessageType.WriteOffNotification
import models.TestMessage

import scala.xml.NodeSeq

object Messages {

  type GenerateMessage = () => NodeSeq

  val guaranteeRef = s"${Strings.numeric(2)}GB${Strings.alphanumeric(13)}"

  object Arrival {

    val SupportedMessageTypes: Map[TestMessage, GenerateMessage] = Map(
      TestMessage(ArrivalRejection.code)          -> generateIE008Message,
      TestMessage(GoodsReleased.code)             -> generateIE025Message,
      TestMessage(UnloadingPermission.code)       -> generateIE043Message,
      TestMessage(UnloadingRemarksRejection.code) -> generateIE058Message
    )

    def generateIE008Message(): NodeSeq = {
      val xml =
        <CC008A>
          <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
          <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
          <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
          <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
          <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
          <MesTypMES20>{Strings.alphanumeric(1, 6)}</MesTypMES20>
          <HEAHEA>
            <DocNumHEA5>{Strings.alphanumeric(1, 21)}</DocNumHEA5>
            <ArrRejDatHEA142>{Strings.numeric8()}</ArrRejDatHEA142>
          </HEAHEA>
        </CC008A>

      xml
    }

    def generateIE025Message(): NodeSeq = {
      val xml =
        <CC025A>
          <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
          <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
          <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
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

      xml
    }

    def generateIE043Message(): NodeSeq = {
      val xml =
        <CC043A>
          <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
          <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
          <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
          <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
          <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
          <MesTypMES20>{Strings.alphanumeric(1, 6)}</MesTypMES20>
        </CC043A>

      xml
    }

    def generateIE058Message(): NodeSeq = {
      val xml =
        <CC058A>
          <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
          <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
          <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
          <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
          <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
          <MesTypMES20>{Strings.alphanumeric(1, 6)}</MesTypMES20>
          <HEAHEA>
            <DocNumHEA5>{Strings.alphanumeric(1, 21)}</DocNumHEA5>
            <UnlRemRejDatHEA218>{Strings.numeric8()}</UnlRemRejDatHEA218>
          </HEAHEA>
        </CC058A>

      xml
    }
  }

  object Departure {

    val SupportedMessageTypes: Map[TestMessage, GenerateMessage] = Map(
      TestMessage(PositiveAcknowledgement.code)     -> generateIE928Message,
      TestMessage(NoReleaseForTransit.code)         -> generateIE051Message,
      TestMessage(ReleaseForTransit.code)           -> generateIE029Message,
      TestMessage(ControlDecisionNotification.code) -> generateIE060Message,
      TestMessage(MrnAllocated.code)                -> generateIE028Message,
      TestMessage(DeclarationRejected.code)         -> generateIE016Message,
      TestMessage(CancellationDecision.code)        -> generateIE009Message,
      TestMessage(WriteOffNotification.code)        -> generateIE045Message,
      TestMessage(GuaranteeNotValid.code)           -> generateIE055Message
    )

    def generateIE928Message(): NodeSeq = {
      val xml =
        <CC928A>
          <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
          <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
          <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
          <SenIdeCodQuaMES4>{Strings.alphanumeric(1, 4)}</SenIdeCodQuaMES4>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <RecIdeCodQuaMES7>{Strings.alphanumeric(1, 4)}</RecIdeCodQuaMES7>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
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

      xml
    }

    def generateIE051Message(): NodeSeq = {
      val xml =
        <CC051B>
          <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
          <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
          <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
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
            <DecDatHEA383>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DecDatHEA383>
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
            <ArrTimTRACUS085>{LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))}</ArrTimTRACUS085>
          </CUSOFFTRARNS>
          <CUSOFFDESEST>
            <RefNumEST1>IT018105</RefNumEST1>
          </CUSOFFDESEST>
          <CONRESERS>
            <ConDatERS14>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</ConDatERS14>
            <ConResCodERS16>B1</ConResCodERS16>
          </CONRESERS>
          <RESOFCON534>
            <DesTOC2>See Header for details</DesTOC2>
            <ConInd424>OT</ConInd424>
          </RESOFCON534>
          <GUAGUA><GuaTypGUA1>1</GuaTypGUA1>
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

      xml
    }

    def generateIE029Message(): NodeSeq = {
      val xml =
        <CC029B>
          <SynIdeMES1>UNOC</SynIdeMES1>
          <SynVerNumMES2>3</SynVerNumMES2>
          <MesSenMES3>NTA.GB</MesSenMES3>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
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
            <DecDatHEA383>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DecDatHEA383>
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
            <ArrTimTRACUS085>{LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))}</ArrTimTRACUS085>
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
            <ConDatERS14>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</ConDatERS14>
            <ConResCodERS16>A3</ConResCodERS16>
            <ConByERS18>Not Controlled</ConByERS18>
            <DatLimERS69>{LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatLimERS69>
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

      xml
    }

    def generateIE060Message(): NodeSeq = {
      val xml =
        <CC060A>
          <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
          <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
          <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
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

      xml
    }

    def generateIE028Message(): NodeSeq = {
      val xml =
        <CC028A>
          <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
          <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
          <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
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

      xml
    }

    def generateIE016Message(): NodeSeq = {
      val xml =
        <CC016A>
          <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
          <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
          <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
          <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
          <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
          <MesTypMES20>{Strings.alphanumeric(1, 6)}</MesTypMES20>
          <HEAHEA>
            <RefNumHEA4>{Strings.alphanumeric(1, 22)}</RefNumHEA4>
            <TypOfDecHEA24>{Strings.alphanumeric(1, 9)}</TypOfDecHEA24>
            <DecRejDatHEA159>{Strings.numeric8()}</DecRejDatHEA159>
          </HEAHEA>
        </CC016A>

      xml
    }

    def generateIE009Message(): NodeSeq = {
      val xml =
        <CC009A>
          <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
          <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
          <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
          <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
          <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
          <MesTypMES20>{Strings.alphanumeric(1, 6)}</MesTypMES20>
          <HEAHEA>
            <DocNumHEA5>{Strings.alphanumeric(1, 21)}</DocNumHEA5>
            <CanIniByCusHEA94>{Strings.numeric(1)}</CanIniByCusHEA94>
            <DatOfCanDecHEA146>{Strings.numeric(8)}</DatOfCanDecHEA146>
          </HEAHEA>
          <TRAPRIPC1/>
          <CUSOFFDEPEPT>
            <RefNumEPT1>{Strings.alphanumeric(8)}</RefNumEPT1>
          </CUSOFFDEPEPT>
        </CC009A>

      xml
    }

    def generateIE045Message(): NodeSeq = {
      val xml =
        <CC045A>
          <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
          <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
          <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
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

      xml
    }

    def generateIE055Message(): NodeSeq = {
      val xml =
        <CC055A>
          <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
          <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
          <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
          <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
          <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
          <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
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
            </INVGUARNS>
          </GUAREF2>
        </CC055A>

      xml
    }
  }
}
