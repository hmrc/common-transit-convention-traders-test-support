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

package test.v2_1.connectors

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import config.AppConfig
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.http.HeaderNames
import play.api.http.Status.ACCEPTED
import play.api.http.Status.BAD_REQUEST
import play.api.http.Status.INTERNAL_SERVER_ERROR
import test.utils.WiremockSuite
import test.v2_1.generators.ItGenerators
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v2_1.connectors.InboundRouterConnector
import v2_1.models.CorrelationId
import v2_1.models.MessageType
import v2_1.models.XMLMessage

import scala.concurrent.ExecutionContext.Implicits.global

class InboundRouterConnectorSpec extends AnyFreeSpec with Matchers with WiremockSuite with ItGenerators with ScalaCheckDrivenPropertyChecks with ScalaFutures {

  override val configure: Seq[(String, Any)] = Seq(
    "microservice.services.transit-movements-router.bearerToken.enabled" -> true,
    "microservice.services.transit-movements-router.bearerToken.token"   -> "bearertokengb"
  )

  "post" - {

    lazy val httpClient = app.injector.instanceOf[HttpClientV2]
    lazy val appConfig  = app.injector.instanceOf[AppConfig]

    def targetUrl(correlationId: CorrelationId) =
      s"/transit-movements-router/movements/${correlationId.toFormattedString}/messages/"

    "a successful injection returns a 202" in forAll(arbitraryCorrelationId.arbitrary, arbitrary[MessageType]) {
      (correlationId, messageType) =>
        implicit val hc: HeaderCarrier = HeaderCarrier()

        lazy val sut = new InboundRouterConnector(httpClient, appConfig)

        server.stubFor(
          post(
            urlEqualTo(targetUrl(correlationId))
          ).willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        // this takes precedence ONLY if the header exists.
        server.stubFor(
          post(
            urlEqualTo(targetUrl(correlationId))
          ).withHeader(HeaderNames.AUTHORIZATION, equalTo("Bearer bearertokengb"))
            .withHeader("X-Message-Type", equalTo(messageType.code))
            .willReturn(aResponse().withStatus(ACCEPTED))
        )

        val result = sut.post(messageType, XMLMessage(<test></test>).wrapped, correlationId)

        whenReady(result) {
          r =>
            r.status mustBe ACCEPTED
        }
    }

    "a failed injection returns a 500" in forAll(arbitraryCorrelationId.arbitrary, arbitrary[MessageType]) {
      (correlationId, messageType) =>
        implicit val hc: HeaderCarrier = HeaderCarrier()

        lazy val sut = new InboundRouterConnector(httpClient, appConfig)

        // this takes precedence ONLY if the header exists.
        server.stubFor(
          post(
            urlEqualTo(targetUrl(correlationId))
          ).withHeader("X-Message-Type", equalTo(messageType.code))
            .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
        )

        val result = sut.post(messageType, XMLMessage(<test></test>).wrapped, correlationId)

        whenReady(result) {
          r =>
            r.status mustBe INTERNAL_SERVER_ERROR
        }
    }

  }

  override protected def portConfigKey: String = "microservice.services.transit-movements-router.port"
}
