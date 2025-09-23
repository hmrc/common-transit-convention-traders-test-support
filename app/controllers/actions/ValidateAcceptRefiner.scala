/*
 * Copyright 2025 HM Revenue & Customs
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

import models.VersionHeader
import models.errors.PresentationError
import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.Sink
import org.apache.pekko.stream.scaladsl.Source
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.mvc.Results.Status
import play.api.mvc.ActionRefiner
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.WrappedRequest

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

final case class ValidatedVersionedRequest[T](
  versionedHeader: VersionHeader,
  authenticatedRequest: AuthRequest[T]
) extends WrappedRequest[T](authenticatedRequest)

final class ValidateAcceptRefiner @Inject() (implicit val ec: ExecutionContext, mat: Materializer)
    extends ActionRefiner[AuthRequest, ValidatedVersionedRequest] {

  private def validateAcceptHeader(authenticatedRequest: AuthRequest[?]): Either[PresentationError, VersionHeader] =
    for {
      acceptHeaderStr <-
        authenticatedRequest.headers
          .get(HeaderNames.ACCEPT)
          .toRight(PresentationError.unsupportedMediaTypeError("An accept header is required"))
      version <-
        VersionHeader
          .fromString(acceptHeaderStr)
          .toRight(PresentationError.unsupportedMediaTypeError(s"Accept header $acceptHeaderStr is not supported"))
    } yield version

  def refine[T](authenticatedRequest: AuthRequest[T]): Future[Either[Result, ValidatedVersionedRequest[T]]] =
    validateAcceptHeader(authenticatedRequest) match {
      case Left(err) =>
        clearSource(authenticatedRequest)
        Future.successful(Left(Status(err.code.statusCode)(Json.toJson(err))))
      case Right(versionHeader) =>
        Future.successful(Right(ValidatedVersionedRequest(versionHeader, authenticatedRequest)))
    }

  private def clearSource(request: Request[?]): Unit =
    request.body match {
      case source: Source[_, _] => val _ = source.runWith(Sink.ignore)
      case _                    => ()
    }

  override protected def executionContext: ExecutionContext = ec
}
