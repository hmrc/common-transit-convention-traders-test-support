package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import models.DepartureId
import models.MessageType
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

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

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.post(MessageType.PositiveAcknowledgement.code, "<document></document>", departureId).futureValue

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

        implicit val hc            = HeaderCarrier()
        implicit val requestHeader = FakeRequest()

        val result = connector.post(MessageType.PositiveAcknowledgement.code, "<document></document>", departureId).futureValue

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

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.post(MessageType.PositiveAcknowledgement.code, "<document></document>", departureId).futureValue

      result.status mustEqual BAD_REQUEST
    }
  }

  "get" - {
    "must return OK when departure is found" in {
      val connector = app.injector.instanceOf[DepartureConnector]

      server.stubFor(
        get(urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1"))
          .willReturn(aResponse().withStatus(OK)))

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get(departureId).futureValue

      result.status mustEqual OK
    }

    "must return HttpResponse with a not found if not found" in {
      val connector = app.injector.instanceOf[DepartureConnector]
      server.stubFor(
        get(urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1"))
          .willReturn(aResponse().withStatus(NOT_FOUND)))

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get(departureId).futureValue

      result.status mustEqual NOT_FOUND
    }

    "must return HttpResponse with a bad request if there is a bad request" in {
      val connector = app.injector.instanceOf[DepartureConnector]
      server.stubFor(
        get(urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1"))
          .willReturn(aResponse().withStatus(BAD_REQUEST)))

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get(departureId).futureValue

      result.status mustEqual BAD_REQUEST
    }

    "must return HttpResponse with an internal server if there is an internal server error" in {
      val connector = app.injector.instanceOf[DepartureConnector]
      server.stubFor(
        get(urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1"))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR)))

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get(departureId).futureValue

      result.status mustEqual INTERNAL_SERVER_ERROR
    }
  }

  override protected def portConfigKey: String = "microservice.services.transits-movements-trader-at-departure.port"
}
