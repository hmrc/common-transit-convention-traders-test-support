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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import models.MessageType
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import utils.WiremockSuite

import scala.concurrent.ExecutionContext.Implicits.global

class InboundRouterConnectorSpec extends AnyFreeSpec with Matchers with WiremockSuite with ScalaFutures with IntegrationPatience with ScalaCheckPropertyChecks {
  private val itemId: Int = 1

  "post" - {
    "must return CREATED when post is successful" in {
      val connector = app.injector.instanceOf[InboundRouterConnector]

      server.stubFor(
        post(
          urlEqualTo("/transit-movements-trader-router/messages"))
          .withHeader("X-Message-Recipient", equalTo("MDTP-ARR-1-1"))
          .withHeader("X-Message-Type", equalTo("IE008"))
          .willReturn(aResponse().withStatus(CREATED))
      )

      implicit val hc = HeaderCarrier()

      val result = connector.post(MessageType.ArrivalRejection, "<document></document>", itemId).futureValue

      result.status mustEqual CREATED
    }

    "must return INTERNAL_SERVER_ERROR when post" - {
      "returns INTERNAL_SERVER_ERROR" in {
        val connector = app.injector.instanceOf[InboundRouterConnector]

        server.stubFor(
          post(
            urlEqualTo("/transit-movements-trader-router/messages"))
            .withHeader("X-Message-Recipient", equalTo("MDTP-ARR-1-1"))
            .withHeader("X-Message-Type", equalTo("IE008"))
            .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
        )

        implicit val hc = HeaderCarrier()

        val result = connector.post(MessageType.ArrivalRejection, "<document></document>", itemId).futureValue

        result.status mustEqual INTERNAL_SERVER_ERROR
      }

    }

    "must return BAD_REQUEST when post returns BAD_REQUEST" in {
      val connector = app.injector.instanceOf[InboundRouterConnector]

      server.stubFor(
        post(
          urlEqualTo("/transit-movements-trader-router/messages"))
          .withHeader("X-Message-Recipient", equalTo("MDTP-DEP-1-1"))
          .withHeader("X-Message-Type", equalTo("IE016"))
          .willReturn(aResponse().withStatus(BAD_REQUEST))
      )

      implicit val hc = HeaderCarrier()

      val result = connector.post(MessageType.DeclarationRejected, "<document></document>", itemId).futureValue

      result.status mustEqual BAD_REQUEST
    }
  }

  override protected def portConfigKey: String = "microservice.services.transit-movements-trader-router.port"
}