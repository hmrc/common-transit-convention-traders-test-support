package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import models.ArrivalId
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class ArrivalConnectorSpec extends AnyFreeSpec with Matchers with WiremockSuite with ScalaFutures with IntegrationPatience with ScalaCheckPropertyChecks {
  private val arrivalId = new ArrivalId(1)

  "get" - {
    "must return OK when arrival is found" in {
      val connector = app.injector.instanceOf[ArrivalConnector]

      server.stubFor(get(urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1"))
        .willReturn(aResponse().withStatus(OK)))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get(arrivalId).futureValue

      result.status mustEqual OK
    }

    "must return HttpResponse with a not found if not found" in {
      val connector = app.injector.instanceOf[ArrivalConnector]
      server.stubFor(get(urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1"))
        .willReturn(aResponse().withStatus(NOT_FOUND)))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get(arrivalId).futureValue

      result.status mustEqual NOT_FOUND
    }

    "must return HttpResponse with a bad request if there is a bad request" in {
      val connector = app.injector.instanceOf[ArrivalConnector]
      server.stubFor(get(urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1"))
        .willReturn(aResponse().withStatus(BAD_REQUEST)))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get(arrivalId).futureValue

      result.status mustEqual BAD_REQUEST
    }

    "must return HttpResponse with an internal server if there is an internal server error" in {
      val connector = app.injector.instanceOf[ArrivalConnector]
      server.stubFor(get(urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1"))
        .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR)))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.get(arrivalId).futureValue

      result.status mustEqual INTERNAL_SERVER_ERROR
    }
  }

  override protected def portConfigKey: String = "microservice.services.transit-movements-trader-at-destination.port"
}
