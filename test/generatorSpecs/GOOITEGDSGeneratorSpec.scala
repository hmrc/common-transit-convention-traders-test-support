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

package generatorSpecs

import base.SpecBase
import generators.GOOITEGDSGenerator
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class GOOITEGDSGeneratorSpec extends SpecBase with ScalaCheckPropertyChecks with BeforeAndAfterEach {

  "generate" - {
    "must return requested number of goods nodes" in {
      val application = baseApplicationBuilder.build()

      val xml = application.injector.instanceOf[GOOITEGDSGenerator].generate(5, 1, 1)
      (xml \\ "GOOITEGDS").length mustEqual 5
    }

    "must return products and special mentions for each goods node" in {
      val application = baseApplicationBuilder.build()

      val xml = application.injector.instanceOf[GOOITEGDSGenerator].generate(5, 2, 3)
      (xml \\ "GOOITEGDS").length mustEqual 5
      (xml \\ "PRODOCDC2").length mustEqual 10
      (xml \\ "SPEMENMT2").length mustEqual 15
    }
  }
}
