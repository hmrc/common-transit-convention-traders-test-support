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

package v2.controllers

import com.google.inject.ImplementedBy
import controllers.actions.AuthAction
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import v2.models.DepartureId

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@ImplementedBy(classOf[DepartureTestMessagesController])
trait V2DepartureTestMessagesController {
  def injectEISResponse(departureId: DepartureId): Action[JsValue]
}

class DepartureTestMessagesController @Inject()(cc: ControllerComponents, authAction: AuthAction)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with V2DepartureTestMessagesController {

  override def injectEISResponse(departureId: DepartureId): Action[JsValue] = (authAction).async(parse.json) {
    _ =>
      Future(NotImplemented)
  }
}
