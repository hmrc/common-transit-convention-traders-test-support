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

package config

import javax.inject.Inject
import javax.inject.Singleton
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject() (config: Configuration, servicesConfig: ServicesConfig) {

  val transitMovementsUrl: String       = servicesConfig.baseUrl("transit-movements")
  val transitMovementsRouterUrl: String = servicesConfig.baseUrl("transit-movements-router")

  lazy val bearerTokenEnabled: Boolean = config.get[Boolean]("microservice.services.transit-movements-router.bearerToken.enabled")
  lazy val bearerTokenToken: String    = config.get[String]("microservice.services.transit-movements-router.bearerToken.token")

  lazy val internalAuthToken: String = config.get[String]("internal-auth.token")
}
