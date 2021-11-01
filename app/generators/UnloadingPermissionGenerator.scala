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

package generators

import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.LocalTime

import com.google.inject.Inject
import models.generation.UnloadingPermissionGenInstructions
import utils.Strings

import scala.xml.NodeSeq

class UnloadingPermissionGenerator @Inject()(GOOITEGDSGenerator: GOOITEGDSGenerator, sealGenerator: SealGenerator) {

  def generate(instructions: UnloadingPermissionGenInstructions): NodeSeq = {
    val xml =
      <CC043A>
        <SynIdeMES1>{Strings.alpha(4)}</SynIdeMES1>
        <SynVerNumMES2>{Strings.numeric(1)}</SynVerNumMES2>
        <MesSenMES3>{Strings.alphanumeric(1, 35)}</MesSenMES3>
        <MesRecMES6>{Strings.alphanumeric(1, 35)}</MesRecMES6>
        <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
        <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
        <IntConRefMES11>{Strings.alphanumeric(1, 14)}</IntConRefMES11>
        <AppRefMES14>{Strings.alphanumeric(1, 14)}</AppRefMES14>
        <TesIndMES18>{Strings.numeric(1)}</TesIndMES18>
        <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
        <MesTypMES20>{Strings.alphanumeric(1, 6)}</MesTypMES20>
        <HEAHEA>
          <DocNumHEA5>{Strings.alphanumeric(1, 21)}</DocNumHEA5>
          <TypOfDecHEA24>{Strings.alphanumeric(1, 9)}</TypOfDecHEA24>
          <CouOfDesCodHEA30>{Strings.alpha(2)}</CouOfDesCodHEA30>
          <CouOfDisCodHEA55>{Strings.alpha(2)}</CouOfDisCodHEA55>
          <ConIndHEA96>{Strings.numeric(1)}</ConIndHEA96>
          <AccDatHEA158>{Strings.numeric(8)}</AccDatHEA158>
          <TotNumOfIteHEA305>{Strings.numeric(1, 5)}</TotNumOfIteHEA305>
          <TotNumOfPacHEA306>{Strings.numeric(1, 7)}</TotNumOfPacHEA306>
          <TotGroMasHEA307>{Strings.numeric(1, 11)}</TotGroMasHEA307>
        </HEAHEA>
        <TRADESTRD>
          <NamTRD7>{Strings.alphanumeric(1, 35)}</NamTRD7>
          <StrAndNumTRD22>{Strings.alphanumeric(1, 35)}</StrAndNumTRD22>
          <PosCodTRD23>{Strings.alphanumeric(9)}</PosCodTRD23>
          <CitTRD24>{Strings.alphanumeric(1, 35)}</CitTRD24>
          <CouTRD25>{Strings.alpha(2)}</CouTRD25>
          <TINTRD59>{Strings.alphanumeric(1, 17)}</TINTRD59>
        </TRADESTRD>
        <CUSOFFDEPEPT>
          <RefNumEPT1>{Strings.alphanumeric(8)}</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFPREOFFRES>
          <RefNumRES1>{Strings.alphanumeric(8)}</RefNumRES1>
        </CUSOFFPREOFFRES>
        <SEAINFSLI>
          <SeaNumSLI2>{Strings.numeric(4)}</SeaNumSLI2>
          {sealGenerator.generate(instructions.sealsCount)}
        </SEAINFSLI>
        {GOOITEGDSGenerator.generate(instructions.goodsCount, instructions.productCount, instructions.specialMentionsCount)}
      </CC043A>

    xml
  }
}
