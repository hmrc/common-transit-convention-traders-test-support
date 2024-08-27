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

package v2_1.services

import cats.data.EitherT
import com.google.inject.ImplementedBy
import com.google.inject.Inject
import v2_1.generators.ArrivalMessageGenerator
import v2_1.generators.DepartureMessageGenerator
import v2_1.generators.MessageGenerator
import v2_1.models.MessageType
import v2_1.models.MovementId
import v2_1.models.MovementType
import v2_1.models.XMLMessage
import v2_1.models.errors.MessageGenerationError

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@ImplementedBy(classOf[MessageGenerationServiceImpl])
trait MessageGenerationService {

  def generateMessage(messageType: MessageType, movementType: MovementType, movementId: MovementId)(implicit
    ec: ExecutionContext
  ): EitherT[Future, MessageGenerationError, XMLMessage]
}

class MessageGenerationServiceImpl @Inject() (
  arrivalMessageGenerator: ArrivalMessageGenerator,
  departureMessageGenerator: DepartureMessageGenerator
) extends MessageGenerationService {

  val generators: PartialFunction[MovementType, MessageGenerator] = {
    case MovementType.Arrival   => arrivalMessageGenerator
    case MovementType.Departure => departureMessageGenerator
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
