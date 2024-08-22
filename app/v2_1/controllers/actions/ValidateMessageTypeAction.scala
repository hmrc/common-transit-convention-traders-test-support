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

import play.api.libs.json.Json
import v2_1.models.MessageType
import v2_1.models.request.MessageRequest
import play.api.mvc.ActionRefiner
import play.api.mvc.Result
import play.api.mvc.Results.NotImplemented
import v2_1.models.errors.PresentationError

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ValidateMessageTypeAction(messageTypes: Seq[MessageType])(implicit val executionContext: ExecutionContext)
    extends ActionRefiner[MessageRequest, MessageRequest] {

  override protected def refine[A](request: MessageRequest[A]): Future[Either[Result, MessageRequest[A]]] =
    if (messageTypes.contains(request.messageType)) Future.successful(Right(request))
    else
      Future.successful(Left(NotImplemented(Json.toJson(PresentationError.notImplementedError(s"API has not been implemented")))))

}