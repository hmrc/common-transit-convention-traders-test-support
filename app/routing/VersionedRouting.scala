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

package routing

import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.Sink
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import play.api.http.HeaderNames
import play.api.http.HttpVerbs
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.BaseController
import play.api.mvc.Request
import v2_1.controllers.stream.StreamingParsers
import v2_1.models.errors.PresentationError
import scala.concurrent.Future

trait VersionedRouting {
  self: BaseController & StreamingParsers =>

  def route(routes: PartialFunction[Option[String], Action[?]])(implicit materializer: Materializer): Action[Source[ByteString, ?]] =
    Action.async(streamFromMemory) {
      (request: Request[Source[ByteString, ?]]) =>
        routes
          .lift(request.headers.get(HeaderNames.ACCEPT))
          .map {
            action =>
              request.method match {
                case HttpVerbs.GET | HttpVerbs.HEAD | HttpVerbs.DELETE | HttpVerbs.OPTIONS =>
                  // For the above verbs, we don't want to send a body,
                  // however, in case we have one, we still need to drain the body.
                  request.body.to(Sink.ignore).run()
                  val headersWithoutContentType = request.headers.remove(CONTENT_TYPE)
                  action(request.withHeaders(headersWithoutContentType)).run()
                case _ =>
                  action(request).run(request.body)
              }
          }
          .getOrElse {
            request.body.to(Sink.ignore).run()
            Future.successful(
              UnsupportedMediaType(
                Json.toJson(
                  PresentationError.unsupportedMediaTypeError(
                    request.headers
                      .get("accept")
                      .map(
                        header => s"Accept header $header is not supported!"
                      )
                      .getOrElse("An accept header is required!")
                  )
                )
              )
            )
          }
    }

  // This simulates what Play will do if a binding fails, with the addition of the "code" field
  // that we use elsewhere.
  def bindingFailureAction(message: String)(implicit materializer: Materializer): Action[Source[ByteString, ?]] =
    Action.async(streamFromMemory) {
      implicit request =>
        request.body.runWith(Sink.ignore)
        Future.successful(Status(BAD_REQUEST)(Json.toJson(PresentationError.bindingBadRequestError(message))))
    }
}
