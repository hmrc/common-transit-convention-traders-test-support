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

import controllers.actions.AuthRequest
import play.api.mvc.AnyContent
import play.api.mvc.BodyParser
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.test.Helpers.stubBodyParser
import v2_1.controllers.actions.AuthAction

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object FakeAuthAction extends AuthAction {
  override def parser: BodyParser[AnyContent] = stubBodyParser()

  override def invokeBlock[A](request: Request[A], block: AuthRequest[A] => Future[Result]): Future[Result] =
    block(AuthRequest(request, "abc"))

  override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global
}