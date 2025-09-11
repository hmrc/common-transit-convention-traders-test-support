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

package services

import cats.data.EitherT
import com.google.inject.Inject
import generators.MessageGenerator
import models.MessageType
import models.MovementId
import models.MovementType
import models.XMLMessage
import models.errors.MessageGenerationError
import versioned.v2_1.generators.ArrivalMessageGenerator as V2_1_ArrivalMessageGenerator
import versioned.v2_1.generators.DepartureMessageGenerator as V2_1_DepartureMessageGenerator

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class MessageGenerationService @Inject() (
  v2_1_arrivalMessageGenerator: V2_1_ArrivalMessageGenerator,
  v2_1_departureMessageGenerator: V2_1_DepartureMessageGenerator
) {

  val generators: PartialFunction[MovementType, MessageGenerator] = {
    case MovementType.Arrival   => v2_1_arrivalMessageGenerator
    case MovementType.Departure => v2_1_departureMessageGenerator
  }

  def generateMessage(messageType: MessageType, movementType: MovementType, movementId: MovementId)(implicit
    ec: ExecutionContext
  ): EitherT[Future, MessageGenerationError, XMLMessage] =
    EitherT.fromEither[Future](
      generators(movementType)
        .generate(movementId)
        .lift(messageType)
        .toRight(MessageGenerationError.MessageTypeNotSupported(messageType))
    )

}
