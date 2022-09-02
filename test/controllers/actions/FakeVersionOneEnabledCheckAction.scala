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

package controllers.actions
import play.api.mvc.AnyContent
import play.api.mvc.BodyParser
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.test.Helpers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.global

class FakeVersionOneEnabledCheckAction extends VersionOneEnabledCheckAction {
  override protected def filter[A](request: Request[A]): Future[Option[Result]] = Future.successful(None)

  override def parser: BodyParser[AnyContent] = Helpers.stubControllerComponents().parsers.defaultBodyParser

  override protected def executionContext: ExecutionContext = global
}