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

package v2.controllers.actions

import v2.models.MessageType
import v2.models.request.MessageRequest
import play.api.mvc.ActionRefiner
import play.api.mvc.Result
import play.api.mvc.Results.NotImplemented

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ValidateDepartureMessageTypeAction @Inject()()(implicit val executionContext: ExecutionContext) extends ActionRefiner[MessageRequest, MessageRequest] {
  override protected def refine[A](request: MessageRequest[A]): Future[Either[Result, MessageRequest[A]]] =
    if (MessageType.departureMessages.contains(request.messageType)) {
      Future.successful(Right(request))
    } else {
      Future.successful(Left(NotImplemented))
    }
}