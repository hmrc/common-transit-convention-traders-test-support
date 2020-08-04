package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import models.{DepartureId, MessageType}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.test.Helpers._

class DepartureConnectorSpec extends AnyFreeSpec with Matchers with WiremockSuite with ScalaFutures with IntegrationPatience with ScalaCheckPropertyChecks {
  private val departureId = new DepartureId(1)

  "post" - {
    "must return CREATED when post is successful" in {
      val connector = app.injector.instanceOf[DepartureConnector]

      server.stubFor(
        post(
          urlEqualTo("/transits-movements-trader-at-departure/movements/departures/MDTP-1-1/messages/eis")
        ).willReturn(aResponse().withStatus(CREATED))
      )

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.post(MessageType.PositiveAcknowledgement.code, "<document></document>", departureId, 1).futureValue

      result.status mustEqual CREATED
    }

    "must return INTERNAL_SERVER_ERROR when post" - {
      "returns INTERNAL_SERVER_ERROR" in {
        val connector = app.injector.instanceOf[DepartureConnector]

        server.stubFor(
          post(
            urlEqualTo("/transits-movements-trader-at-departure/movements/departures/MDTP-1-1/messages/eis")
          ).willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
        )

        implicit val hc = HeaderCarrier()
        implicit val requestHeader = FakeRequest()

        val result = connector.post(MessageType.PositiveAcknowledgement.code, "<document></document>", departureId, 1).futureValue

        result.status mustEqual INTERNAL_SERVER_ERROR
      }

    }

    "must return BAD_REQUEST when post returns BAD_REQUEST" in {
      val connector = app.injector.instanceOf[DepartureConnector]

      server.stubFor(
        post(
          urlEqualTo("/transits-movements-trader-at-departure/movements/departures/MDTP-1-1/messages/eis")
        ).willReturn(aResponse().withStatus(BAD_REQUEST))
      )

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.post(MessageType.PositiveAcknowledgement.code, "<document></document>", departureId, 1).futureValue

      result.status mustEqual BAD_REQUEST
    }
  }

  override protected def portConfigKey: String = "microservice.services.transits-movements-trader-at-departure.port"
}
