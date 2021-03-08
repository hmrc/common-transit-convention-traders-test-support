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
import models.generation.SealGenerator
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
        <MesIdeMES19>{Strings.alphanumeric(1, 14)}</MesIdeMES19>
        <MesTypMES20>{Strings.alphanumeric(1, 6)}</MesTypMES20>
        {sealGenerator.generate(instructions.sealsCount)}
        {GOOITEGDSGenerator.generate(instructions.goodsCount, instructions.productCount, instructions.specialMentionsCount)}
      </CC043A>

    xml
  }

}
