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

package test.connectors

import java.time.LocalDateTime
import com.github.tomakehurst.wiremock.client.WireMock._
import connectors.ArrivalMessageConnector
import models.ChannelType
import models.MessageType.ArrivalRejection
import models.domain.MovementMessage
import models.generation.TestMessage
import org.scalacheck.Gen
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import test.utils.WiremockSuite
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class ArrivalMessageConnectorSpec
    extends AnyFreeSpec
    with Matchers
    with WiremockSuite
    with ScalaFutures
    with IntegrationPatience
    with ScalaCheckPropertyChecks {

  "get" - {
    "must return MovementMessage when message is found" in {
      val channel: ChannelType = Gen.oneOf(ChannelType.values).sample.get
      val connector            = app.injector.instanceOf[ArrivalMessageConnector]
      val movement = MovementMessage("/transit-movements-trader-at-destination/movements/arrivals/1/messages/1", LocalDateTime.now, "abc", <test>default</test>)
      server.stubFor(
        get(
          urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1/messages/1")
        ).willReturn(
          aResponse()
            .withStatus(OK)
            .withBody(Json.toJson(movement).toString())
        )
      )

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get("1", "1", channel).futureValue

      result mustEqual Right(movement)
    }

    "must return HttpResponse with an internal server error if there is a model mismatch" in {
      val channel: ChannelType = Gen.oneOf(ChannelType.values).sample.get
      val connector            = app.injector.instanceOf[ArrivalMessageConnector]

      val response = TestMessage(ArrivalRejection)
      server.stubFor(
        get(
          urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1/messages/1")
        ).willReturn(
          aResponse()
            .withStatus(OK)
            .withBody(Json.toJson(response).toString())
        )
      )

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get("1", "1", channel).futureValue

      result.isLeft mustEqual true
      result.left.map {
        x =>
          x.status mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "must return HttpResponse with a not found if not found" in {
      val channel: ChannelType = Gen.oneOf(ChannelType.values).sample.get
      val connector            = app.injector.instanceOf[ArrivalMessageConnector]
      server.stubFor(
        get(
          urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1/messages/1")
        ).willReturn(aResponse().withStatus(NOT_FOUND))
      )

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get("1", "1", channel).futureValue

      result.isLeft mustEqual true
      result.left.map {
        x =>
          x.status mustEqual NOT_FOUND
      }
    }

    "must return HttpResponse with a bad request if there is a bad request" in {
      val channel: ChannelType = Gen.oneOf(ChannelType.values).sample.get
      val connector            = app.injector.instanceOf[ArrivalMessageConnector]
      server.stubFor(
        get(
          urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1/messages/1")
        ).willReturn(aResponse().withStatus(BAD_REQUEST))
      )

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get("1", "1", channel).futureValue

      result.isLeft mustEqual true
      result.left.map {
        x =>
          x.status mustEqual BAD_REQUEST
      }
    }

    "must return HttpResponse with an internal server if if there is an internal server error" in {
      val channel: ChannelType = Gen.oneOf(ChannelType.values).sample.get
      val connector            = app.injector.instanceOf[ArrivalMessageConnector]
      server.stubFor(
        get(
          urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1/messages/1")
        ).willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get("1", "1", channel).futureValue

      result.isLeft mustEqual true
      result.left.map {
        x =>
          x.status mustEqual INTERNAL_SERVER_ERROR
      }
    }
  }

  override protected def portConfigKey: String = "microservice.services.transit-movements-trader-at-destination.port"
}
