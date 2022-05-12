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

import utils.Strings

import scala.annotation.tailrec
import scala.xml.NodeSeq

class ProductGenerator {

  def generate(count: Int): NodeSeq = {
    @tailrec
    def gen_internal(count: Int, accumulator: NodeSeq): NodeSeq =
      count match {
        case 0 => accumulator
        case _ => {
          gen_internal(
            count - 1,
            accumulator ++
              <PRODOCDC2>
              <DocTypDC21>18</DocTypDC21>
              <DocRefDC23>{Strings.alphanumeric(35)}</DocRefDC23>
              <DocRefDCLNG>{Strings.alpha(2)}</DocRefDCLNG>
              <ComOfInfDC25>{Strings.alphanumeric(26)}</ComOfInfDC25>
              <ComOfInfDC25LNG>{Strings.alpha(2)}</ComOfInfDC25LNG>
            </PRODOCDC2>
          )
        }
      }

    gen_internal(count, NodeSeq.Empty)
  }

}
