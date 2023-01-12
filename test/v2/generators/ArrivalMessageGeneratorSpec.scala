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

package v2.generators

import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import v2.models.MessageType.GoodsReleaseNotification
import v2.models.MessageType.RejectionFromOfficeOfDestination
import v2.models.MessageType.UnloadingPermission
import v2.models.MovementId
import v2.models.XMLMessage

import java.io.StringReader
import java.time.Clock
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

class ArrivalMessageGeneratorSpec extends AnyFreeSpec with Matchers with OptionValues {
  "A generator" - {
    val generator = new ArrivalMessageGeneratorImpl(Clock.systemUTC())
    val arrivalId = Gen.stringOfN(16, Gen.alphaNumChar).map(MovementId(_)).sample.value

    "when a goods release notification is requested" - {
      "should produce a valid IE025 message" in {
        validate("cc025c", generator.generate(arrivalId)(GoodsReleaseNotification))
      }
    }

    "when an unloading permission is requested" - {
      "should produce a valid IE043 message" in {
        validate("cc043c", generator.generate(arrivalId)(UnloadingPermission))
      }
    }

    "when an rejection of office of destination is requested" - {
      "should produce a valid IE057 message" in {
        validate("cc057c", generator.generate(arrivalId)(RejectionFromOfficeOfDestination))
      }
    }

  }

  private def validate(xsdRoot: String, xml: XMLMessage): Unit = {
    val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schemaUrl     = getClass.getResource(s"/xsd/phase5/${xsdRoot.toLowerCase}.xsd")
    val schema        = schemaFactory.newSchema(schemaUrl)
    val validator     = schema.newValidator()
    validator.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
    validator.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    validator.setFeature("http://xml.org/sax/features/external-general-entities", false)
    validator.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    val reader = new StringReader(xml.value.mkString)
    try validator.validate(new StreamSource(reader))
    finally reader.close()
  }

}
