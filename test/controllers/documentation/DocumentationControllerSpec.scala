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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.NOT_FOUND
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers.defaultAwaitTimeout
import play.api.test.Helpers.status
import play.api.test.Helpers.stubControllerComponents

class DocumentationControllerSpec extends AnyFreeSpec with Matchers with OptionValues with ScalaFutures with MockitoSugar with Results with BeforeAndAfterEach {

  val appConfig    = mock[AppConfig]
  val assets       = mock[Assets]
  val resultAction = mock[Action[AnyContent]]

  val sut = new DocumentationController(appConfig, assets, stubControllerComponents())

  override def beforeEach(): Unit = {
    reset(appConfig)
    reset(assets)
  }

  "when version one is enabled" - {

    "when getting a version one raml asset, accept it" in {

      when(appConfig.enableVersionOne).thenReturn(true)
      when(assets.at(any(), any(), any())).thenReturn(resultAction)

      val result = sut.raml("1.0", "a")
      result mustBe resultAction
      verify(assets, times(1)).at(any(), any(), any())
    }

    "when getting a version two raml asset, accept it" in {

      when(appConfig.enableVersionOne).thenReturn(true)
      when(assets.at(any(), any(), any())).thenReturn(resultAction)

      val result = sut.raml("2.0", "a")
      result mustBe resultAction
      verify(assets, times(1)).at(any(), any(), any())
    }

  }

  "when version two is disabled" - {

    "when getting a version one raml asset, reject it with Not Found" in {

      when(appConfig.enableVersionOne).thenReturn(false)
      when(assets.at(any(), any(), any())).thenReturn(resultAction)

      val result = sut.raml("1.0", "a")
      result must not be resultAction
      verify(assets, times(0)).at(any(), any(), any())
      status(result.apply(FakeRequest())) mustBe NOT_FOUND
    }

    "when getting a version two raml asset, accept it" in {

      when(appConfig.enableVersionOne).thenReturn(false)
      when(assets.at(any(), any(), any())).thenReturn(resultAction)

      val result = sut.raml("2.0", "a")
      result mustBe resultAction
      verify(assets, times(1)).at(any(), any(), any())
    }

  }

}
