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
import models.ChannelType
import models.request.ChannelRequest
import org.scalacheck.Gen
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

case class FakeChannelAction @Inject() ()(implicit override val executionContext: ExecutionContext) extends ChannelAction {

  override protected def refine[A](request: Request[A]): Future[Either[Result, ChannelRequest[A]]] = {
    val channel: ChannelType = Gen.oneOf(ChannelType.values).sample.get
    Future.successful(Right(new ChannelRequest(request, channel)))
  }
}
