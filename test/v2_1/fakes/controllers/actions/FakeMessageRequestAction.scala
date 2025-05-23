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

package v2_1.fakes.controllers.actions

import play.api.mvc.Result
import v2_1.controllers.actions.AuthRequest
import v2_1.controllers.actions.MessageRequestAction
import v2_1.models.EORINumber
import v2_1.models.MessageType
import v2_1.models.request.MessageRequest

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

case class FakeMessageRequestAction(messageType: MessageType) extends MessageRequestAction {

  override protected def refine[A](request: AuthRequest[A]): Future[Either[Result, MessageRequest[A]]] =
    Future.successful(Right(MessageRequest(request.request, EORINumber(request.eori), messageType)))

  override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global
}
