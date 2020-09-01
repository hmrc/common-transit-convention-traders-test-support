/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers

import connectors.DepartureConnector
import controllers.actions.AuthAction
import controllers.actions.GeneratedMessageRequest
import controllers.actions.ValidateDepartureMessageTypeAction
import javax.inject.Inject
import models.DepartureId
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import utils.ResponseHelper

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DepartureTestMessagesController @Inject()(cc: ControllerComponents,
                                                departureConnector: DepartureConnector,
                                                authAction: AuthAction,
                                                validateDepartureMessageTypeAction: ValidateDepartureMessageTypeAction)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with HttpErrorFunctions
    with ResponseHelper {

  def injectEISResponse(departureId: DepartureId): Action[JsValue] =
    (authAction andThen validateDepartureMessageTypeAction).async(parse.json) {
      implicit request: GeneratedMessageRequest[JsValue] =>
        departureConnector
          .get(departureId)
          .flatMap {
            getResponse =>
              getResponse.status match {
                case status if is2xx(status) =>
                  departureConnector
                    .post(request.testMessage.messageType, request.generatedMessage.toString(), departureId)
                    .map {
                      postResponse =>
                        postResponse.status match {
                          case status if is2xx(status) =>
                            Created
                          case _ =>
                            handleNon2xx(postResponse)
                        }
                    }
                    .recover {
                      case _ =>
                        InternalServerError
                    }
                case _ =>
                  Future.successful(handleNon2xx(getResponse))
              }
          }
          .recover {
            case _ =>
              InternalServerError
          }
    }
}
