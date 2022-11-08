package v2.connectors

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import config.AppConfig
import config.Constants
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.http.Status.ACCEPTED
import play.api.http.Status.BAD_REQUEST
import play.api.http.Status.INTERNAL_SERVER_ERROR
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import utils.WiremockSuite
import v2.generators.ItGenerators
import v2.models.MessageType
import v2.models.MovementId
import v2.models.XMLMessage

import scala.concurrent.ExecutionContext.Implicits.global

class InboundRouterConnectorSpec extends AnyFreeSpec with Matchers with WiremockSuite with ItGenerators with ScalaCheckDrivenPropertyChecks with ScalaFutures {

  "post" - {

    lazy val httpClient = app.injector.instanceOf[HttpClient]
    lazy val appConfig = app.injector.instanceOf[AppConfig]

    def targetUrl(movementId: MovementId) =
      s"/transit-movements-router/movements/${movementId.value}-${Constants.DefaultTriggerId}/messages/"


    "a successful injection returns a 202" in forAll(arbitrary[MovementId], arbitrary[MessageType]) {
      (movementId, messageType) =>
        implicit val hc: HeaderCarrier = HeaderCarrier()

        lazy val sut = new InboundRouterConnector(httpClient, appConfig)

        server.stubFor(
          post(
            urlEqualTo(targetUrl(movementId))
          )
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        // this takes precedence ONLY if the header exists.
        server.stubFor(
          post(
            urlEqualTo(targetUrl(movementId))
          )
            .withHeader("X-Message-Type", equalTo(messageType.code))
            .willReturn(aResponse().withStatus(ACCEPTED))
        )

        val result = sut.post(messageType, XMLMessage(<test></test>).wrapped, movementId)

        whenReady(result) {
          r => r.status mustBe ACCEPTED
        }
    }

    "a failed injection returns a 500" in forAll(arbitrary[MovementId], arbitrary[MessageType]) {
      (movementId, messageType) =>
        implicit val hc: HeaderCarrier = HeaderCarrier()

        lazy val sut = new InboundRouterConnector(httpClient, appConfig)

        // this takes precedence ONLY if the header exists.
        server.stubFor(
          post(
            urlEqualTo(targetUrl(movementId))
          )
            .withHeader("X-Message-Type", equalTo(messageType.code))
            .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
        )

        val result = sut.post(messageType, XMLMessage(<test></test>).wrapped, movementId)

        whenReady(result) {
          r => r.status mustBe INTERNAL_SERVER_ERROR
        }
    }

  }

  override protected def portConfigKey: String = "microservice.services.transit-movements-router.port"
}
