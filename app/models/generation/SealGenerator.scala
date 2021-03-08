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

package models.generation

import utils.Strings

import scala.annotation.tailrec
import scala.xml.NodeSeq

class SealGenerator {

  def generate(sealCount: Int): NodeSeq = {
    @tailrec
    def gen_internal(count: Int, accumulator: NodeSeq): NodeSeq =
      count match {
        case 0 => accumulator
        case _ => {
          gen_internal(count - 1,
                       accumulator ++
                         <SeaIdeSID1>{Strings.alphanumeric(20)}</SeaIdeSID1>
            <SeaIdeSID1LNG>{Strings.alpha(2)}</SeaIdeSID1LNG>)
        }
      }

    gen_internal(sealCount, NodeSeq.Empty)
  }
}
