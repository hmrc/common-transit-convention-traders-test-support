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

import v2_1.utils.CallOps.CallOps

sealed abstract class MovementType(val name: String, val urlFragment: String) extends Product with Serializable {
  def generateBaseUrl(movementId: MovementId): String
}

object MovementType {

  case object Arrival extends MovementType("arrival", "arrivals") {

    override def generateBaseUrl(movementId: MovementId): String =
      routing.routes.DeparturesRouter.injectEISResponse(movementId.value, None).urlWithContext
  }

  case object Departure extends MovementType("departure", "departures") {

    override def generateBaseUrl(movementId: MovementId): String =
      routing.routes.ArrivalsRouter.injectEISResponse(movementId.value, None).urlWithContext
  }

  def find(value: String): Option[MovementType] = values.find(_.name == value)

  val values: Seq[MovementType] = Seq(Arrival, Departure)
}
