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

package v2_1.models

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import scala.xml.Utility.trim
import scala.xml.XML

class XMLMessageSpec extends AnyFreeSpec with Matchers {

  "XMLMessage#wrapped" - {
    "should get a wrapped message" in {
      val node = """<ncts:message xmlns:ncts="http://ncts.dgtaxud.ec"><test>1</test><test2>2</test2></ncts:message>"""
      val expected = <n1:TraderChannelResponse xmlns:txd="http://ncts.dgtaxud.ec"
                                               xmlns:n1="http://www.hmrc.gov.uk/eis/ncts5/v1"
                                               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                               xsi:schemaLocation="http://www.hmrc.gov.uk/eis/ncts5/v1 EIS_WrapperV10_TraderChannelSubmission-51.8.xsd">
        <txd:message>
          <test>1</test> <test2>2</test2>
        </txd:message>
      </n1:TraderChannelResponse>
      val sut    = XMLMessage(XML.loadString(node))
      val result = sut.wrapped

      trim(result.value.head) mustBe trim(expected)
    }

    "should get a wrapped message if a node is blank" in {
      val node = """<ncts:message xmlns:ncts="http://ncts.dgtaxud.ec"><test></test><test2>2</test2></ncts:message>"""
      val expected = <n1:TraderChannelResponse xmlns:txd="http://ncts.dgtaxud.ec"
                                               xmlns:n1="http://www.hmrc.gov.uk/eis/ncts5/v1"
                                               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                               xsi:schemaLocation="http://www.hmrc.gov.uk/eis/ncts5/v1 EIS_WrapperV10_TraderChannelSubmission-51.8.xsd">
        <txd:message>
          <test></test> <test2>2</test2>
        </txd:message>
      </n1:TraderChannelResponse>
      val sut    = XMLMessage(XML.loadString(node))
      val result = sut.wrapped

      trim(result.value.head) mustBe trim(expected)
    }
  }
}
