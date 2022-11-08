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

package routing

import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.google.inject.Inject
import controllers.V1DepartureTestMessagesController
import models.DepartureId
import play.api.mvc.Action
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.PathBindable
import v2.controllers.V2TestMessagesController
import v2.controllers.stream.StreamingParsers
import v2.models.Bindings

class DeparturesRouter @Inject()(
  val controllerComponents: ControllerComponents,
  v1Departures: V1DepartureTestMessagesController,
  v2Departures: V2TestMessagesController
)(implicit
  val materializer: Materializer)
    extends BaseController
    with StreamingParsers
    with VersionedRouting {

  def injectEISResponse(departureId: String): Action[Source[ByteString, _]] = route {
    case Some(VersionedRouting.VERSION_2_ACCEPT_HEADER_VALUE) =>
      (for {
        convertedDepartureId <- Bindings.movementIdBinding.bind("departureId", departureId)
      } yield convertedDepartureId).fold(
        bindingFailureAction(_),
        convertedDepartureId => v2Departures.sendDepartureResponse(convertedDepartureId)
      )
    case _ =>
      (for {
        convertedDepartureId <- implicitly[PathBindable[DepartureId]].bind("departureId", departureId)
      } yield convertedDepartureId).fold(
        bindingFailureAction(_),
        convertedDepartureId => v1Departures.injectEISResponse(convertedDepartureId)
      )

  }
}
