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

package v2_1.controllers.actions

import com.google.inject.ImplementedBy
import com.google.inject.Inject
import com.google.inject.Singleton
import v2_1.models.MessageType
import v2_1.models.MovementType

import scala.concurrent.ExecutionContext

@ImplementedBy(classOf[ValidateMessageTypeActionProviderImpl])
trait ValidateMessageTypeActionProvider {
  def apply(movementType: MovementType): ValidateMessageTypeAction
}

@Singleton
class ValidateMessageTypeActionProviderImpl @Inject() ()(implicit ec: ExecutionContext) extends ValidateMessageTypeActionProvider {

  lazy val arrivalMessageTypeAction   = new ValidateMessageTypeAction(MessageType.arrivalMessages)
  lazy val departureMessageTypeAction = new ValidateMessageTypeAction(MessageType.departureMessages)

  def apply(movementType: MovementType) = movementType match {
    case MovementType.Arrival   => arrivalMessageTypeAction
    case MovementType.Departure => departureMessageTypeAction
  }

}
