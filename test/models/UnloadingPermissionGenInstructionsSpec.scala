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

package models

import base.SpecBase
import models.generation.UnloadingPermissionGenInstructions
import org.scalatest.BeforeAndAfterEach
import org.scalatest.EitherValues
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class UnloadingPermissionGenInstructionsSpec extends SpecBase with ScalaCheckPropertyChecks with BeforeAndAfterEach with EitherValues {

  "validate" - {
    "must return Right(instructions) if the inputs are valid" in {
      val instructions = UnloadingPermissionGenInstructions(10, 10, 10, 10)

      UnloadingPermissionGenInstructions.validate(instructions) mustBe a[Right[_, UnloadingPermissionGenInstructions]]
    }

    "must return Left(message) if the goods count over 999" in {
      val instructions = UnloadingPermissionGenInstructions(1000, 10, 10, 10)

      UnloadingPermissionGenInstructions.validate(instructions) mustBe a[Left[String, _]]
    }

    "must return Left(message) if the product count over 99" in {
      val instructions = UnloadingPermissionGenInstructions(10, 100, 10, 10)

      UnloadingPermissionGenInstructions.validate(instructions) mustBe a[Left[String, _]]
    }

    "must return Left(message) if the special mentions count over 99" in {
      val instructions = UnloadingPermissionGenInstructions(10, 10, 100, 10)

      UnloadingPermissionGenInstructions.validate(instructions) mustBe a[Left[String, _]]
    }

    "must return Left(message) if the seals count over 9999" in {
      val instructions = UnloadingPermissionGenInstructions(10, 10, 10, 10000)

      UnloadingPermissionGenInstructions.validate(instructions) mustBe a[Left[String, _]]
    }
  }

}
