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

package controllers.actions

import javax.inject.Inject
import models.TestMessage
import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.mvc.ActionRefiner
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.Results.BadRequest
import play.api.mvc.Results.NotImplemented
import utils.Messages
import utils.Messages.GenerateMessage

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ValidateArrivalMessageTypeAction @Inject()()(implicit val executionContext: ExecutionContext) extends ActionRefiner[Request, GeneratedMessageRequest] {
  override protected def refine[A](request: Request[A]): Future[Either[Result, GeneratedMessageRequest[A]]] =
    request.body match {
      case body: JsValue =>
        body.validate[TestMessage] match {
          case JsError(_) =>
            Future.successful(Left(BadRequest))
          case JsSuccess(testMessage, _) =>
            Messages.Arrival.SupportedMessageTypes.get(testMessage) match {
              case None =>
                Future.successful(Left(NotImplemented))
              case Some(generateMessage: GenerateMessage) =>
                Future.successful(Right(GeneratedMessageRequest(request, testMessage, generateMessage())))
            }
        }
      case _ =>
        Future.successful(Left(BadRequest))
    }
}
