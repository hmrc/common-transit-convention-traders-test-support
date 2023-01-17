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

package v2.models

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import scala.xml.Utility.trim

class XMLMessageSpec extends AnyFreeSpec with Matchers {

  "XMLMessage#wrapped should get a wrapped message" in {
    val node     = <message>
      <test>1</test> <test2>2</test2>
    </message>
    val expected = <TraderChannelResponse>
      <message>
        <test>1</test> <test2>2</test2>
      </message>
    </TraderChannelResponse>
    val sut      = XMLMessage(node)
    val result   = sut.wrapped

    trim(result.value.head) mustBe trim(expected)
  }
}
