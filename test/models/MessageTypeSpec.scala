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

import models.MessageType
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import models.MessageType.AmendmentAcceptance
import models.MessageType.GoodsReleaseNotification
import models.MessageType.InvalidationDecision
import models.MessageType.PositiveAcknowledgement
import models.MessageType.UnloadingPermission

class MessageTypeSpec extends AnyFreeSpec with Matchers with MockitoSugar with OptionValues with ScalaCheckDrivenPropertyChecks {
  "MessageType must contain" - {
    "GoodsReleaseNotification" in {
      MessageType.values must contain(GoodsReleaseNotification)
      GoodsReleaseNotification.code mustEqual "IE025"
      GoodsReleaseNotification.rootNode mustEqual "CC025C"
      MessageType.arrivalMessages must contain(GoodsReleaseNotification)
    }

    "UnloadingPermission" in {
      MessageType.values must contain(UnloadingPermission)
      UnloadingPermission.code mustEqual "IE043"
      UnloadingPermission.rootNode mustEqual "CC043C"
      MessageType.arrivalMessages must contain(UnloadingPermission)
    }

    "AmendmentAcceptance" in {
      MessageType.values must contain(AmendmentAcceptance)
      AmendmentAcceptance.code mustEqual "IE004"
      AmendmentAcceptance.rootNode mustEqual "CC004C"
      MessageType.departureMessages must contain(AmendmentAcceptance)
    }

    "InvalidationDecision" in {
      MessageType.values must contain(InvalidationDecision)
      InvalidationDecision.code mustEqual "IE009"
      InvalidationDecision.rootNode mustEqual "CC009C"
      MessageType.departureMessages must contain(InvalidationDecision)
    }

    "PositiveAcknowledgement" in {
      MessageType.values must contain(PositiveAcknowledgement)
      PositiveAcknowledgement.code mustEqual "IE928"
      PositiveAcknowledgement.rootNode mustEqual "CC928C"
      MessageType.departureMessages must contain(PositiveAcknowledgement)
    }
  }

  "find" - {
    "must return None when junk is provided" in forAll(Gen.stringOfN(6, Gen.alphaNumChar)) {
      code =>
        MessageType.findByCode(code) must not be defined
    }

    "must return the correct message type when a correct code is provided" in forAll(Gen.oneOf(MessageType.values)) {
      messageType =>
        MessageType.findByCode(messageType.code) mustBe Some(messageType)
    }
  }
}
