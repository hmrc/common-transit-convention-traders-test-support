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

import com.google.inject.Inject
import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import play.api.mvc.Action
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import v2_1.controllers.TestMessagesController
import v2_1.controllers.stream.StreamingParsers
import v2_1.models.Bindings

class ArrivalsRouter @Inject() (
  val controllerComponents: ControllerComponents,
  arrivals: TestMessagesController
)(implicit val materializer: Materializer)
    extends BaseController
    with StreamingParsers
    with VersionedRouting {

  def injectEISResponse(arrivalId: String, messageId: Option[String]): Action[Source[ByteString, ?]] = route {
    _ =>
      (for {
        convertedArrivalId <- Bindings.movementIdBinding.bind("arrivalId", arrivalId)
      } yield convertedArrivalId).fold(
        bindingFailureAction(_),
        convertedArrivalId => arrivals.sendArrivalsResponse(convertedArrivalId, messageId)
      )
  }
}
