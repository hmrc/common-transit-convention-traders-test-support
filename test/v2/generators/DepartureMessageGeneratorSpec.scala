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

package v2.generators

import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import v2.models.DepartureId
import v2.models.MessageType.MRNAllocated
import v2.models.MessageType.PositiveAcknowledgement

import java.io.StringReader
import java.time.Clock
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import scala.xml.NodeSeq

class DepartureMessageGeneratorSpec extends AnyFreeSpec with Matchers with OptionValues {
  "A generator" - {
    val generator   = new DepartureMessageGenerator(Clock.systemUTC())
    val departureId = Gen.stringOfN(16, Gen.alphaNumChar).map(DepartureId(_)).sample.value

    "when a positive acknowledgement is requested" - {
      "should produce a valid IE928 message" in {
        validate("cc928c", generator.generate(departureId)(PositiveAcknowledgement))
      }
    }

    "when supplied with message type MRNAllocated" - {
      "should produce an IE028 Message" in {
        validate("cc028c", generator.generate(departureId)(MRNAllocated))
      }
    }
  }

  private def validate(xsdRoot: String, xml: NodeSeq): Unit = {
    val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schemaUrl     = getClass.getResource(s"/xsd/phase5/${xsdRoot.toLowerCase}.xsd")
    val schema        = schemaFactory.newSchema(schemaUrl)
    val validator     = schema.newValidator()
    validator.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
    validator.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    validator.setFeature("http://xml.org/sax/features/external-general-entities", false)
    validator.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    val reader = new StringReader(xml.mkString)
    try {
      validator.validate(new StreamSource(reader))
    } finally {
      reader.close()
    }
  }

}
