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
import play.api.http.Status.ACCEPTED
import play.api.http.Status.INTERNAL_SERVER_ERROR
import test.v2_1.generators.ItGenerators
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import utils.GuiceWiremockSuite
import utils.WiremockSuite
import v2_1.connectors.InboundRouterConnector
import v2_1.models.CorrelationId
import v2_1.models.MessageType
import v2_1.models.XMLMessage

import scala.concurrent.ExecutionContext.Implicits.global

class InboundRouterConnectorSpec
    extends AnyFreeSpec
    with Matchers
    with WiremockSuite
    with GuiceWiremockSuite
    with ItGenerators
    with ScalaCheckDrivenPropertyChecks
    with ScalaFutures {

  override val configurationOverride: Seq[(String, Any)] = Seq(
    "microservice.services.transit-movements-router.bearerToken.enabled" -> true,
    "microservice.services.transit-movements-router.bearerToken.token"   -> "bearertokengb"
  )

  "post" - {

    lazy val httpClient = mockApp.injector.instanceOf[HttpClientV2]
    lazy val appConfig  = mockApp.injector.instanceOf[AppConfig]

    "a successful injection returns a 202" in forAll(arbitraryCorrelationId.arbitrary, arbitrary[MessageType]) {
      (correlationId, messageType) =>
        implicit val hc: HeaderCarrier = HeaderCarrier()

        lazy val sut: InboundRouterConnector = new InboundRouterConnector(httpClient, appConfig)
        lazy val result                      = sut.post(messageType, XMLMessage(<test></test>).wrapped, correlationId).futureValue

        stub(correlationId, ACCEPTED, messageType)

        result.status mustBe ACCEPTED

    }

    "a failed injection returns a 500" in forAll(arbitraryCorrelationId.arbitrary, arbitrary[MessageType]) {
      (correlationId, messageType) =>
        implicit val hc: HeaderCarrier = HeaderCarrier()

        lazy val sut    = new InboundRouterConnector(httpClient, appConfig)
        lazy val result = sut.post(messageType, XMLMessage(<test></test>).wrapped, correlationId).futureValue

        stub(correlationId, INTERNAL_SERVER_ERROR, messageType)

        result.status mustBe INTERNAL_SERVER_ERROR
    }

  }

  private def targetUrl(correlationId: CorrelationId) =
    s"/transit-movements-router/movements/${correlationId.toFormattedString}/messages/"

  private def stub(correlationId: CorrelationId, responseStatus: Int, messageType: MessageType): Unit =
    server.stubFor(
      post(
        urlEqualTo(targetUrl(correlationId))
      ).withHeader("X-Message-Type", equalTo(messageType.code))
        .willReturn(aResponse().withStatus(responseStatus))
    )

  override protected def portConfigKey: Seq[String] = Seq("microservice.services.transit-movements-router.port")
}
