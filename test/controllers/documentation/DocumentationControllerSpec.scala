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

package controllers.documentation

import config.AppConfig
import controllers.Assets
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.{eq => eqTo}
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

  val v1definitionAction = mock[Action[AnyContent]]
  val v2definitionAction = mock[Action[AnyContent]]

  override def beforeEach(): Unit = {
    reset(appConfig)
    reset(assets)
  }

  "when version one is enabled" - {

    val sut = new DocumentationController(appConfig, assets, stubControllerComponents())

    "when getting the definition file, get the one for both v1 and v2" in {
      when(appConfig.enableVersionOne).thenReturn(true)
      when(assets.at(any(), eqTo("definition.json"), any())).thenReturn(v2definitionAction)
      when(assets.at(any(), eqTo("definition_with_v1.json"), any())).thenReturn(v1definitionAction)

      sut.definition() mustBe v1definitionAction

      verify(assets, times(0)).at(any(), eqTo("definition.json"), any())
      verify(assets, times(1)).at(any(), eqTo("definition_with_v1.json"), any())
    }

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

  "when version one is disabled" - {

    val sut = new DocumentationController(appConfig, assets, stubControllerComponents())

    "when getting the definition file, get the one for only v2" in {
      when(appConfig.enableVersionOne).thenReturn(false)
      when(assets.at(any(), eqTo("definition.json"), any())).thenReturn(v2definitionAction)
      when(assets.at(any(), eqTo("definition_with_v1.json"), any())).thenReturn(v1definitionAction)

      sut.definition() mustBe v2definitionAction

      verify(assets, times(1)).at(any(), eqTo("definition.json"), any())
      verify(assets, times(0)).at(any(), eqTo("definition_with_v1.json"), any())
    }

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
