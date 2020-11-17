package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import models.{Arrival, ArrivalId, ArrivalWithMessages, MessageType, MovementMessage, ResponseArrival}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import java.time.LocalDateTime

import play.api.libs.json.Json

class ArrivalConnectorSpec extends AnyFreeSpec with Matchers with WiremockSuite with ScalaFutures with IntegrationPatience with ScalaCheckPropertyChecks {
  private val arrivalId = new ArrivalId(1)

  "post" - {
    "must return CREATED when post is successful" in {
      val connector = app.injector.instanceOf[ArrivalConnector]

      server.stubFor(
        post(
          urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/MDTP-1-1/messages/eis")
        ).willReturn(aResponse().withStatus(CREATED))
      )

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.post(MessageType.ArrivalRejection.code, "<document></document>", arrivalId).futureValue

      result.status mustEqual CREATED
    }

    "must return INTERNAL_SERVER_ERROR when post" - {
      "returns INTERNAL_SERVER_ERROR" in {
        val connector = app.injector.instanceOf[ArrivalConnector]

        server.stubFor(
          post(
            urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/MDTP-1-1/messages/eis")
          ).willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
        )

        implicit val hc = HeaderCarrier()
        implicit val requestHeader = FakeRequest()

        val result = connector.post(MessageType.ArrivalRejection.code, "<document></document>", arrivalId).futureValue

        result.status mustEqual INTERNAL_SERVER_ERROR
      }

    }

    "must return BAD_REQUEST when post returns BAD_REQUEST" in {
      val connector = app.injector.instanceOf[ArrivalConnector]

      server.stubFor(
        post(
          urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/MDTP-1-1/messages/eis")
        ).willReturn(aResponse().withStatus(BAD_REQUEST))
      )

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.post(MessageType.PositiveAcknowledgement.code, "<document></document>", arrivalId).futureValue

      result.status mustEqual BAD_REQUEST
    }
  }

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

  "getMessages" - {
    "must return Arrival when arrival is found" in {
      val connector = app.injector.instanceOf[ArrivalConnector]
      val arrival = ArrivalWithMessages(1, "/movements/arrivals/1", "/movements/arrivals/1/messages", "MRN", "status", LocalDateTime.now, LocalDateTime.now,
        Seq(
          MovementMessage("/movements/arrivals/1/messages/1", LocalDateTime.now, "abc", <test>default</test>),
          MovementMessage("/movements/arrivals/1/messages/2", LocalDateTime.now, "abc", <test>default</test>)
        ))

      server.stubFor(
        get(
          urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1/messages")
        ).willReturn(aResponse().withStatus(OK)
          .withBody(Json.toJson(arrival).toString())))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.getMessages(arrivalId).futureValue

      result mustEqual Right(arrival)
    }

    "must return HttpResponse with an internal server error if there is a model mismatch" in {
      val connector = app.injector.instanceOf[ArrivalConnector]
      val arrival = Arrival(1, "/movements/arrivals/1", "/movements/arrivals/1/messages/1", "MRN", "status", LocalDateTime.now, LocalDateTime.now)

      val response = ResponseArrival(arrival)
      server.stubFor(
        get(
          urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1/messages")
        ).willReturn(aResponse().withStatus(OK)
          .withBody(Json.toJson(response).toString())))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.getMessages(arrivalId).futureValue

      result.isLeft mustEqual true
      result.left.map { x => x.status mustEqual INTERNAL_SERVER_ERROR }
    }

    "must return HttpResponse with a not found if not found" in {
      val connector = app.injector.instanceOf[ArrivalConnector]
      server.stubFor(
        get(
          urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1/messages")
        ).willReturn(aResponse().withStatus(NOT_FOUND)))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.getMessages(arrivalId).futureValue

      result.isLeft mustEqual true
      result.left.map { x => x.status mustEqual NOT_FOUND }
    }

    "must return HttpResponse with a bad request if there is a bad request" in {
      val connector = app.injector.instanceOf[ArrivalConnector]
      server.stubFor(
        get(
          urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1/messages")
        ).willReturn(aResponse().withStatus(BAD_REQUEST)))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.getMessages(arrivalId).futureValue

      result.isLeft mustEqual true
      result.left.map { x => x.status mustEqual BAD_REQUEST }
    }

    "must return HttpResponse with an internal server if there is an internal server error" in {
      val connector = app.injector.instanceOf[ArrivalConnector]
      server.stubFor(
        get(
          urlEqualTo("/transit-movements-trader-at-destination/movements/arrivals/1/messages")
        ).willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR)))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.getMessages(arrivalId).futureValue

      result.isLeft mustEqual true
      result.left.map { x => x.status mustEqual INTERNAL_SERVER_ERROR }
    }
  }

  override protected def portConfigKey: String = "microservice.services.transit-movements-trader-at-destination.port"
}
