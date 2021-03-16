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

import com.google.inject.Inject
import utils.Strings

import scala.annotation.tailrec
import scala.xml.NodeSeq

class GOOITEGDSGenerator @Inject()(productGenerator: ProductGenerator, specialMentionsGenerator: SpecialMentionsGenerator, sealGenerator: SealGenerator) {

  def generate(goodsCount: Int, productCount: Int, specialMentionsCount: Int): NodeSeq = {
    @tailrec
    def gen_internal(count: Int, accumulator: NodeSeq): NodeSeq =
      count match {
        case 0 => accumulator
        case _ => {
          gen_internal(
            count - 1,
            accumulator ++
              <GOOITEGDS>
                <IteNumGDS7>{Strings.numeric(5)}</IteNumGDS7>
                <ComCodTarCodGDS10>{Strings.alphanumeric(22)}</ComCodTarCodGDS10>
                <DecTypGDS15>{Strings.alphanumeric(9)}</DecTypGDS15>
                <GooDesGDS23>{Strings.alphanumeric(280)}</GooDesGDS23>
                <GooDesGDS23LNG>{Strings.alpha(2)}</GooDesGDS23LNG>
                <GroMasGDS46>{Strings.decimalMax12()}</GroMasGDS46>
                <NetMasGDS48>{Strings.decimalMax12()}</NetMasGDS48>
                <CouOfDisGDS58>{Strings.alpha(2)}</CouOfDisGDS58>
                <CouOfDesGDS59>{Strings.alpha(2)}</CouOfDesGDS59>
                {productGenerator.generate(productCount)}
                {specialMentionsGenerator.generate(specialMentionsCount)}
              </GOOITEGDS>
          )
        }
      }

    gen_internal(goodsCount, NodeSeq.Empty)
  }
}
