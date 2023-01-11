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
import models.ChannelType.api
import models.ChannelType.web
import models.request.ChannelRequest
import play.api.mvc.Results.BadRequest
import play.api.mvc.ActionRefiner
import play.api.mvc.Request
import play.api.mvc.Result

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ChannelAction @Inject()()(implicit val executionContext: ExecutionContext) extends ActionRefiner[Request, ChannelRequest] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, ChannelRequest[A]]] = {

    val channelOpt: Option[ChannelType] = request.headers.get("channel") match {
      case Some(channel) if channel.equals(api.toString) => Some(api)
      case Some(channel) if channel.equals(web.toString) => Some(web)
      case _                                             => Some(api) //TODO: Retire defaulting(should be NONE)
    }

    channelOpt match {
      case None =>
        Future.successful(Left(BadRequest("Missing channel header or incorrect value specified in channel header")))
      case Some(channel) =>
        Future.successful(Right(new ChannelRequest(request, channel)))
    }

  }
}
