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
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import config.AppConfig
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.http.HeaderNames
import play.api.http.Status.NOT_FOUND
import play.api.http.Status.OK
import play.api.libs.json.Json
import test.v2_1.generators.ItGenerators
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.http.client.HttpClientV2
import utils.GuiceWiremockSuite
import utils.WiremockSuite
import v2_1.connectors.MovementConnector
import v2_1.models.*

import java.time.OffsetDateTime
import scala.concurrent.ExecutionContext.Implicits.global

class MovementConnectorSpec
    extends AnyFreeSpec
    with Matchers
    with WiremockSuite
    with GuiceWiremockSuite
    with ItGenerators
    with ScalaCheckDrivenPropertyChecks
    with ScalaFutures
    with OptionValues {

  lazy val httpClient: HttpClientV2 = mockApp.injector.instanceOf[HttpClientV2]
  lazy val appConfig: AppConfig     = mockApp.injector.instanceOf[AppConfig]

  val token: String = Gen.alphaNumStr.sample.get

  override val configurationOverride: Seq[(String, Any)] = Seq(
    "internal-auth.token" -> token
  )

  "getMovement" - {

    def getMovementUri(movementType: MovementType, eori: EORINumber, movementId: MovementId) =
      s"/transit-movements/traders/${eori.value}/movements/${movementType.urlFragment}/${movementId.value}"

    "a successful retrieval should result in a Movement object being returned" in forAll(
      arbitrary[MovementType],
      arbitrary[EORINumber],
      arbitrary[MovementId],
      Gen.option(arbitrary[MovementReferenceNumber]),
      arbitrary[OffsetDateTime]
    ) {
      (movementType, eori, movementId, maybeMrn, offsetDateTime) =>
        implicit val hc: HeaderCarrier = HeaderCarrier()

        server.stubFor(
          get(
            urlEqualTo(getMovementUri(movementType, eori, movementId))
          ).withHeader(HeaderNames.AUTHORIZATION, equalTo(token))
            .willReturn(
              aResponse()
                .withBody(
                  Json.stringify(
                    Json.obj(
                      "_id"                     -> movementId.value,
                      "enrollmentEORINumber"    -> eori.value,
                      "movementEORINumber"      -> eori.value,
                      "movementReferenceNumber" -> maybeMrn,
                      "created"                 -> offsetDateTime,
                      "updated"                 -> offsetDateTime
                    )
                  )
                )
                .withStatus(OK)
            )
        )
        val sut = new MovementConnector(httpClient, appConfig)
        whenReady(sut.getMovement(movementType, eori, movementId)) {
          r =>
            r mustBe Movement(
              _id = movementId,
              enrollmentEORINumber = eori,
              movementEORINumber = eori,
              movementReferenceNumber = maybeMrn,
              created = offsetDateTime,
              updated = offsetDateTime
            )
        }
    }

    "a failed retrieval should result in an error" in forAll(
      arbitrary[MovementType],
      arbitrary[EORINumber],
      arbitrary[MovementId]
    ) {
      (movementType, eori, movementId) =>
        implicit val hc: HeaderCarrier = HeaderCarrier()
        server.stubFor(
          get(
            urlEqualTo(getMovementUri(movementType, eori, movementId))
          ).withHeader(HeaderNames.AUTHORIZATION, equalTo(token))
            .willReturn(aResponse().withStatus(NOT_FOUND))
        )
        val sut = new MovementConnector(httpClient, appConfig)
        val result = sut
          .getMovement(movementType, eori, movementId)
          .map(
            _ => fail("Should not have succeeded")
          )
          .recover {
            case UpstreamErrorResponse(_, NOT_FOUND, _, _) => ()
          }
        whenReady(result) {
          r => // success means we got the response we were looking for
        }
    }

  }

  "getMessage" - {

    def getMessageUri(movementType: MovementType, eori: EORINumber, movementId: MovementId, messageId: MessageId) =
      s"/transit-movements/traders/${eori.value}/movements/${movementType.urlFragment}/${movementId.value}/messages/${messageId.value}"

    "a successful retrieval should result in a Movement object being returned" in forAll(
      arbitrary[MovementType],
      arbitrary[EORINumber],
      arbitrary[MovementId],
      arbitrary[MessageId],
      arbitrary[MessageType],
      arbitrary[OffsetDateTime]
    ) {
      (movementType, eori, movementId, messageId, messageType, offsetDateTime) =>
        val maybeBody                  = Gen.option(Gen.alphaNumStr).sample.value // we can only do six generators above, so here's the seventh
        implicit val hc: HeaderCarrier = HeaderCarrier()

        server.stubFor(
          get(
            urlEqualTo(getMessageUri(movementType, eori, movementId, messageId))
          ).withHeader(HeaderNames.AUTHORIZATION, equalTo(token))
            .willReturn(
              aResponse()
                .withBody(
                  Json.stringify(
                    Json.obj(
                      "id"          -> messageId.value,
                      "received"    -> offsetDateTime,
                      "messageType" -> messageType,
                      "body"        -> maybeBody
                    )
                  )
                )
                .withStatus(OK)
            )
        )
        val sut = new MovementConnector(httpClient, appConfig)
        whenReady(sut.getMessage(movementType, eori, movementId, messageId)) {
          r =>
            r mustBe Message(
              id = messageId,
              received = offsetDateTime,
              messageType = messageType,
              body = maybeBody
            )
        }
    }

    "a failed retrieval should result in an error" in forAll(
      arbitrary[MovementType],
      arbitrary[EORINumber],
      arbitrary[MovementId],
      arbitrary[MessageId]
    ) {
      (movementType, eori, movementId, messageId) =>
        implicit val hc: HeaderCarrier = HeaderCarrier()

        server.stubFor(
          get(
            urlEqualTo(getMessageUri(movementType, eori, movementId, messageId))
          ).withHeader(HeaderNames.AUTHORIZATION, equalTo(token))
            .willReturn(aResponse().withStatus(NOT_FOUND))
        )
        val sut = new MovementConnector(httpClient, appConfig)
        val result = sut
          .getMessage(movementType, eori, movementId, messageId)
          .map(
            _ => fail("Should not have succeeded")
          )
          .recover {
            case UpstreamErrorResponse(_, NOT_FOUND, _, _) => ()
          }
        whenReady(result) {
          r => // success means we got the response we were looking for
        }
    }

  }

  override protected def portConfigKey: Seq[String] = Seq("microservice.services.transit-movements.port")
}
