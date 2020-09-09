/*
 * Copyright 2020 HM Revenue & Customs
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

package generators

import models.ArrivalId
import models.DepartureId
import models.MessageType
import org.scalacheck.Arbitrary
import org.scalacheck.Gen

trait ModelGenerators extends BaseGenerators with JavaTimeGenerators {

  implicit lazy val arbitraryArrivalId: Arbitrary[ArrivalId] = {
    Arbitrary {
      for {
        id <- intWithMaxLength(9)
      } yield ArrivalId(id)
    }
  }

  implicit lazy val arbitraryDepartureId: Arbitrary[DepartureId] = {
    Arbitrary {
      for {
        id <- intWithMaxLength(9)
      } yield DepartureId(id)
    }
  }

  implicit lazy val arbitraryMessageType: Arbitrary[MessageType] =
    Arbitrary(Gen.oneOf(MessageType.values))
}
