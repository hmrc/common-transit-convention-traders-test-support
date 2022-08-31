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

import config.AppConfig
import org.mockito.Mockito.when
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.BodyParsers
import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents

import scala.concurrent.ExecutionContext.Implicits.global

class VersionOneEnabledCheckActionSpec extends AnyFreeSpec with Matchers with OptionValues with ScalaFutures with MockitoSugar with Results {

  "when version one is enabled, return None to allow the actions to continue" in {
    val appConfig = mock[AppConfig]
    when(appConfig.enableVersionOne).thenReturn(true)
    val sut = new VersionOneEnabledCheckActionImpl(appConfig, new BodyParsers.Default(stubControllerComponents().parsers))
    whenReady(sut.filter(FakeRequest())) {
      _ mustBe None
    }
  }

  "when version one is disabled, return None to allow the actions to continue" in {
    val appConfig = mock[AppConfig]
    when(appConfig.enableVersionOne).thenReturn(false)
    val sut = new VersionOneEnabledCheckActionImpl(appConfig, new BodyParsers.Default(stubControllerComponents().parsers))
    whenReady(sut.filter(FakeRequest())) {
      _ mustBe Some(NotFound)
    }
  }

}
