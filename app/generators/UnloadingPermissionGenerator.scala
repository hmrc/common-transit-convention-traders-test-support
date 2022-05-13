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

package generators

import com.google.inject.Inject
import models.generation.UnloadingPermissionGenInstructions
import utils.Strings

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import scala.xml.NodeSeq

class UnloadingPermissionGenerator @Inject()(GOOITEGDSGenerator: GOOITEGDSGenerator, sealGenerator: SealGenerator) {

  def generate(instructions: UnloadingPermissionGenInstructions): NodeSeq = {
    val xml =
      <CC043A>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesSenMES3>NTA.GB</MesSenMES3>
        <MesRecMES6>
          {Strings.alphanumeric(1, 35)}
        </MesRecMES6>
        <DatOfPreMES9>{LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}</DatOfPreMES9>
        <TimOfPreMES10>{LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))}</TimOfPreMES10>
        <IntConRefMES11>66390912144854</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <TesIndMES18>0</TesIndMES18>
        <MesIdeMES19>66390912144854</MesIdeMES19>
        <MesTypMES20>GB043A</MesTypMES20>
        <HEAHEA>
          <DocNumHEA5>99IT9876AB88901209</DocNumHEA5>
          <TypOfDecHEA24>T1</TypOfDecHEA24>
          <CouOfDesCodHEA30>GB</CouOfDesCodHEA30>
          <CouOfDisCodHEA55>IT</CouOfDisCodHEA55>
          <ConIndHEA96>0</ConIndHEA96>
          <AccDatHEA158>20190912</AccDatHEA158>
          <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
          <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
          <TotGroMasHEA307>1000</TotGroMasHEA307>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>Mancini Carriers</NamPC17>
          <StrAndNumPC122>90 Desio Way</StrAndNumPC122>
          <PosCodPC123>MOD 5JJ</PosCodPC123>
          <CitPC124>Modena</CitPC124>
          <CouPC125>IT</CouPC125>
          <TINPC159>IT444100201000</TINPC159>
        </TRAPRIPC1>
        <TRADESTRD>
          <NamTRD7>The Luggage Carriers</NamTRD7>
          <StrAndNumTRD22>225 Suedopolish Yard,</StrAndNumTRD22>
          <PosCodTRD23>SS8 2BB</PosCodTRD23>
          <CitTRD24>,</CitTRD24>
          <CouTRD25>GB</CouTRD25>
          <TINTRD59>IT444100201000</TINTRD59>
        </TRADESTRD>
        <CUSOFFDEPEPT>
          <RefNumEPT1>IT021100</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFPREOFFRES>
          <RefNumRES1>GB000060</RefNumRES1>
        </CUSOFFPREOFFRES>
        <SEAINFSLI>
          <SeaNumSLI2>
            {Strings.numeric(4)}
          </SeaNumSLI2>{sealGenerator.generate(instructions.sealsCount)}
        </SEAINFSLI>{GOOITEGDSGenerator.generate(instructions.goodsCount, instructions.productCount, instructions.specialMentionsCount)}
      </CC043A>

    xml
  }
}
