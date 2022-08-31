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

package controllers.documentation

import config.AppConfig
import controllers.Assets
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class DocumentationController @Inject()(appConfig: AppConfig, assets: Assets, cc: ControllerComponents) extends BackendController(cc) {

  lazy val definitionAction: Action[AnyContent] =
    if (appConfig.enableVersionOne) assets.at("/public/api", "definition_with_v1.json")
    else assets.at("/public/api", "definition.json")

  def definition(): Action[AnyContent] =
    definitionAction

  def raml(version: String, file: String): Action[AnyContent] =
    if (appConfig.enableVersionOne || version != "1.0") assets.at(s"/public/api/conf/$version", file)
    else Action(NotFound)

}
