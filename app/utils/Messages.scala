/*
 * Copyright 2020 HM Revenue & Customs
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
import java.time.format.DateTimeFormatter

import models.MessageType.ControlDecisionNotification
import models.MessageType.DeclarationRejected
import models.MessageType.MrnAllocated
import models.MessageType.NoReleaseForTransit
import models.MessageType.PositiveAcknowledgement
import models.MessageType.ReleaseForTransit
import models.TestMessage

import scala.xml.NodeSeq

object Messages {

  type GenerateMessage = () => NodeSeq

  val SupportedMessageTypes: Map[TestMessage, GenerateMessage] = Map(
    TestMessage(PositiveAcknowledgement.code)     -> Messages.generateIE928Message,
    TestMessage(NoReleaseForTransit.code)         -> Messages.generateIE051Message,
    TestMessage(ReleaseForTransit.code)           -> Messages.generateIE029Message,
    TestMessage(ControlDecisionNotification.code) -> Messages.generateIE060Message,
    TestMessage(MrnAllocated.code)                -> Messages.generateIE028Message,
    TestMessage(DeclarationRejected.code)         -> Messages.generateIE016Message
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
      <CC051A>
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
          <TypOfDecHEA24>{Strings.alphanumeric(1, 9)}</TypOfDecHEA24>
          <ConIndHEA96>{Strings.numeric(1)}</ConIndHEA96>
          <NCTSAccDocHEA601LNG>{Strings.alpha(2)}</NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305>{Strings.numeric(5)}</TotNumOfIteHEA305>
          <TotGroMasHEA307>{Strings.decimalMax12()}</TotGroMasHEA307>
          <DecDatHEA383>{Strings.numeric8()}</DecDatHEA383>
          <DecPlaHEA394>{Strings.alphanumeric(1, 35)}</DecPlaHEA394>
        </HEAHEA>
        <TRAPRIPC1/>
        <CUSOFFDEPEPT>
          <RefNumEPT1>{Strings.alphanumeric(8)}</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFDESEST>
          <RefNumEST1>{Strings.alphanumeric(8)}</RefNumEST1>
        </CUSOFFDESEST>
        <CONRESERS>
          <ConDatERS14>{Strings.numeric8()}</ConDatERS14>
          <ConResCodERS16>{Strings.alphanumeric(2)}</ConResCodERS16>
        </CONRESERS>
        <GUAGUA>
          <GuaTypGUA1>{Strings.alphanumeric(1)}</GuaTypGUA1>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>{Strings.numeric(1,5)}</IteNumGDS7>
          <GooDesGDS23>{Strings.alphanumeric(1, 280)}</GooDesGDS23>
          <RESOFCONROC>
            <ConIndROC1>{Strings.alphanumeric(2)}</ConIndROC1>
          </RESOFCONROC>
          <PACGS2>
            <KinOfPacGS23>{Strings.alphanumeric(1, 3)}</KinOfPacGS23>
          </PACGS2>
        </GOOITEGDS>
      </CC051A>

    xml
  }

  def generateIE029Message(): NodeSeq = {
    val xml =
      <CC029A>
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
          <TypOfDecHEA24>{Strings.alphanumeric(1, 9)}</TypOfDecHEA24>
          <ConIndHEA96>{Strings.numeric(1)}</ConIndHEA96>
          <NCTRetCopHEA104>{Strings.numeric(1)}</NCTRetCopHEA104>
          <AccDatHEA158>{Strings.numeric8()}</AccDatHEA158>
          <IssDatHEA186>{Strings.numeric8()}</IssDatHEA186>
          <NCTSAccDocHEA601LNG>{Strings.alpha(2)}</NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305>{Strings.numeric(1, 5)}</TotNumOfIteHEA305>
          <TotGroMasHEA307>{Strings.numeric(5)}</TotGroMasHEA307>
          <BinItiHEA246>{Strings.numeric(1)}</BinItiHEA246>
          <DecDatHEA383>{Strings.numeric8()}</DecDatHEA383>
          <DecPlaHEA394>{Strings.alphanumeric(1, 35)}</DecPlaHEA394>
        </HEAHEA>
        <TRAPRIPC1/>
        <CUSOFFDEPEPT>
          <RefNumEPT1>{Strings.alphanumeric(8)}</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFDESEST>
          <RefNumEST1>{Strings.alphanumeric(8)}</RefNumEST1>
        </CUSOFFDESEST>
        <GUAGUA>
          <GuaTypGUA1>{Strings.alphanumeric(1)}</GuaTypGUA1>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>{Strings.numeric(1,5)}</IteNumGDS7>
          <GooDesGDS23>{Strings.alphanumeric(1, 35)}</GooDesGDS23>
        </GOOITEGDS>
      </CC029A>

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
}
