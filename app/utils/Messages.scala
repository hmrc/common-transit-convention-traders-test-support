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

import models.MessageType.PositiveAcknowledgement
import models.TestMessage

import scala.xml.NodeSeq

object Messages {

  type GenerateMessage = () => NodeSeq

  val SupportedMessageTypes: Map[TestMessage, GenerateMessage] = Map(
    TestMessage(PositiveAcknowledgement.code) -> Messages.generateIE928Message
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
}
