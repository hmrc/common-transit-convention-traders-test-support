/*
 * Copyright 2021 HM Revenue & Customs
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

import com.google.inject.Inject
import models.MessageType
import models.MessageType.UnloadingPermission
import models.generation.EmptyGenInstructions
import models.generation.GenInstructions
import models.generation.TestMessage
import models.generation.UnloadingPermissionGenInstructions
import models.request.MessageRequest
import models.request.UnloadingPermissionMessageRequest
import play.api.libs.json.JsError
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.mvc.Results.BadRequest
import play.api.mvc.ActionRefiner
import play.api.mvc.Request
import play.api.mvc.Result

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class MessageRequestAction @Inject()()(implicit val executionContext: ExecutionContext) extends ActionRefiner[Request, MessageRequest] {
  override protected def refine[A](request: Request[A]): Future[Either[Result, MessageRequest[A]]] =
    request.body match {
      case body: JsValue =>
        body.validate[TestMessage] match {
          case JsError(_) =>
            Future.successful(Left(BadRequest))
          case JsSuccess(testMessage, _) =>
            parseGenInstructions(testMessage.messageType, body) match {
              case None               => println(testMessage.messageType); println("badParse"); Future.successful(Left(BadRequest("No instructions found")))
              case Some(instructions) => Future.successful(Right(MessageRequest(request, testMessage.messageType, instructions)))
            }
        }
    }

  private def parseGenInstructions(messageType: MessageType, json: JsValue): Option[GenInstructions] =
    messageType match {
      case UnloadingPermission => println("unloadingPermission"); println(json); extractInstructionsOrNone(json.validate[UnloadingPermissionGenInstructions])
      case _                   => Some(EmptyGenInstructions())
    }

  private def extractInstructionsOrNone(jsResult: JsResult[GenInstructions]): Option[GenInstructions] = jsResult match {
    case JsSuccess(instructions, _) => Some(instructions)
    case _                          => None
  }
}
