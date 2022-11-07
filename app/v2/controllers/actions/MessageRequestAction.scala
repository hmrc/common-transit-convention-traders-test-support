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

import com.google.inject.ImplementedBy
import com.google.inject.Inject
import controllers.actions.AuthRequest
import v2.models.generation.TestMessage
import v2.models.request.MessageRequest
import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.ActionRefiner
import play.api.mvc.Result
import play.api.mvc.Results.BadRequest
import v2.models.EORINumber
import v2.models.errors.PresentationError

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@ImplementedBy(classOf[MessageRequestActionImpl])
trait MessageRequestAction extends ActionRefiner[AuthRequest, MessageRequest]

class MessageRequestActionImpl @Inject()()(implicit val executionContext: ExecutionContext) extends MessageRequestAction {
  override protected def refine[A](request: AuthRequest[A]): Future[Either[Result, MessageRequest[A]]] =
    request.body match {
      case body: JsValue =>
        body.validate[TestMessage] match {
          case JsError(errors) =>
            Future.successful(Left(BadRequest(Json.toJson(PresentationError.badRequestError(errors.mkString)))))
          case JsSuccess(testMessage, _) =>
            Future.successful(Right(MessageRequest(request, EORINumber(request.eori), testMessage.messageType)))
        }
    }
}
