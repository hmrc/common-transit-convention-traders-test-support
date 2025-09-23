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

package models

import base.SpecBase
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.libs.json.Json
import versioned.v2_1.generators.ModelGenerators

class HateoasResponseSpec extends SpecBase with ModelGenerators {

  val xmlMessage = XMLMessage(<test></test>)

  "ensure that an appropriate Hateoas Json object is returned" - {
    "for a departure" in forAll(arbitrary[MovementId], arbitrary[MessageId], Gen.oneOf(MessageType.values)) {
      (movementId, messageId, messageType) =>
        HateoasResponse(MovementType.Departure, movementId, messageType, xmlMessage, messageId) mustBe
          Json.obj(
            "_links" -> Json.obj(
              "self"      -> Json.obj("href" -> s"/customs/transits/movements/departures/${movementId.value}/messages/${messageId.value}"),
              "departure" -> Json.obj("href" -> s"/customs/transits/movements/departures/${movementId.value}")
            ),
            s"departureId" -> movementId.value,
            "messageId"    -> messageId.value,
            "messageType"  -> messageType.code,
            "body"         -> "<test></test>"
          )
    }

    "for an arrival" in forAll(arbitrary[MovementId], arbitrary[MessageId], Gen.oneOf(MessageType.values)) {
      (movementId, messageId, messageType) =>
        HateoasResponse(MovementType.Arrival, movementId, messageType, xmlMessage, messageId) mustBe
          Json.obj(
            "_links" -> Json.obj(
              "self"    -> Json.obj("href" -> s"/customs/transits/movements/arrivals/${movementId.value}/messages/${messageId.value}"),
              "arrival" -> Json.obj("href" -> s"/customs/transits/movements/arrivals/${movementId.value}")
            ),
            s"arrivalId"  -> movementId.value,
            "messageId"   -> messageId.value,
            "messageType" -> messageType.code,
            "body"        -> "<test></test>"
          )
    }
  }

}
