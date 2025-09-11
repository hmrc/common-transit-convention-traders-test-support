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

package controllers.actions

import com.google.inject.Inject
import models.EORINumber
import models.MessageType
import models.MovementType
import models.errors.PresentationError
import models.generation.TestMessage
import models.request.MessageRequest
import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.ActionRefiner
import play.api.mvc.Result
import play.api.mvc.Results.BadRequest
import play.api.mvc.Results.NotImplemented

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class MessageRequestRefiner @Inject() ()(implicit val ec: ExecutionContext) extends ActionRefiner[ValidatedVersionedRequest, MessageRequest] {

  def apply(movementType: MovementType): ActionRefiner[ValidatedVersionedRequest, MessageRequest] =
    new ActionRefiner[ValidatedVersionedRequest, MessageRequest] {
      def refine[T](request: ValidatedVersionedRequest[T]): Future[Either[Result, MessageRequest[T]]] =
        request.body match {
          case body: JsValue =>
            body.validate[TestMessage] match {
              case JsError(errors) =>
                Future.successful(Left(BadRequest(Json.toJson(PresentationError.badRequestError(errors.mkString)))))
              case JsSuccess(testMessage, _) =>
                movementType match {
                  case MovementType.Arrival if MessageType.arrivalMessages.contains(testMessage.messageType) =>
                    Future.successful(
                      Right(MessageRequest(request, EORINumber(request.authenticatedRequest.eori), testMessage.messageType, request.versionedHeader))
                    )
                  case MovementType.Departure if MessageType.departureMessages.contains(testMessage.messageType) =>
                    Future.successful(
                      Right(MessageRequest(request, EORINumber(request.authenticatedRequest.eori), testMessage.messageType, request.versionedHeader))
                    )
                  case _ =>
                    Future.successful(Left(NotImplemented(Json.toJson(PresentationError.notImplementedError(s"API has not been implemented")))))
                }
            }
        }

      override protected def executionContext: ExecutionContext = ec
    }

  override protected def refine[A](request: ValidatedVersionedRequest[A]): Future[Either[Result, MessageRequest[A]]] =
    request.body match {
      case body: JsValue =>
        body.validate[TestMessage] match {
          case JsError(errors) =>
            Future.successful(Left(BadRequest(Json.toJson(PresentationError.badRequestError(errors.mkString)))))
          case JsSuccess(testMessage, _) =>
            Future.successful(Right(MessageRequest(request, EORINumber(request.authenticatedRequest.eori), testMessage.messageType, request.versionedHeader)))
        }
    }

  override protected def executionContext: ExecutionContext = ec
}
