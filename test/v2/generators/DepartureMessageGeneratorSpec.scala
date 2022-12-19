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
import v2.models.MovementId
import v2.models.MessageType.AmendmentAcceptance
import v2.models.MessageType.ControlDecisionNotification
import v2.models.MessageType.GuaranteeNotValid
import v2.models.MessageType.InvalidationDecision
import v2.models.MessageType.MRNAllocated
import v2.models.MessageType.NoReleaseForTransit
import v2.models.MessageType.PositiveAcknowledgement
import v2.models.MessageType.RecoveryNotification
import v2.models.MessageType.RejectionFromOfficeOfDeparture
import v2.models.MessageType.ReleaseForTransit
import v2.models.MessageType.RequestOnNonArrivedMovementDate
import v2.models.XMLMessage

import java.io.StringReader
import java.time.Clock
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

class DepartureMessageGeneratorSpec extends AnyFreeSpec with Matchers with OptionValues {
  "A generator" - {
    val generator   = new DepartureMessageGeneratorImpl(Clock.systemUTC())
    val departureId = Gen.stringOfN(16, Gen.alphaNumChar).map(MovementId(_)).sample.value

    "when an amendment acceptance is requested" - {
      "should produce a valid IE004 message" in {
        validate("cc004c", generator.generate(departureId)(AmendmentAcceptance))
      }
    }

    "when an invalidation decision is requested" - {
      "should produce a valid IE009 message" in {
        validate("cc009c", generator.generate(departureId)(InvalidationDecision))
      }
    }

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

    "when supplied with message type ReleaseForTransit" - {
      "should produce an IE029 Message" in {
        validate("cc029c", generator.generate(departureId)(ReleaseForTransit))
      }
    }

    "when supplied with message type RejectionFromOfficeOfDeparture" - {
      "should produce an IE056 Message" in {
        validate("cc056c", generator.generate(departureId)(RejectionFromOfficeOfDeparture))
      }
    }

    "when supplied with message type ControlDecisionNotification" - {
      "should produce an IE060 Message" in {
        validate("cc060c", generator.generate(departureId)(ControlDecisionNotification))
      }
    }

    "when supplied with message type RecoveryNotification" - {
      "should produce an IE035 Message" in {
        validate("cc035c", generator.generate(departureId)(RecoveryNotification))
      }
    }

    "when supplied with message type NoReleaseForTransit" - {
      "should produce an IE051 Message" in {
        validate("cc051c", generator.generate(departureId)(NoReleaseForTransit))
      }
    }

    "when supplied with message type GuaranteeNotValid" - {
      "should produce an IE055 Message" in {
        validate("cc055c", generator.generate(departureId)(GuaranteeNotValid))
      }
    }

    "when a non-arrived movement date is requested " - {
      "should produce a valid IE140 message" in {
        validate("cc140c", generator.generate(departureId)(RequestOnNonArrivedMovementDate))
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
