package connectors

import java.time.LocalDateTime

import com.github.tomakehurst.wiremock.client.WireMock._
import models.{Departure, DepartureId, DepartureWithMessages, MessageType, MovementMessage, ResponseDeparture}
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json
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

  "getMessages" - {
    "must return Departure when departure is found" in {
      val connector = app.injector.instanceOf[DepartureConnector]
      val departure = DepartureWithMessages(1, "/movements/departures/1", "/movements/departures/1/messages", Some("MRN"), "ref", "status", LocalDateTime.now, LocalDateTime.now,
        Seq(
          MovementMessage("/movements/departures/1/messages/1", LocalDateTime.now, "abc", <test>default</test>),
          MovementMessage("/movements/departures/1/messages/2", LocalDateTime.now, "abc", <test>default</test>)
        ))

      server.stubFor(
        get(
          urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1/messages")
        ).willReturn(aResponse().withStatus(OK)
          .withBody(Json.toJson(departure).toString())))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.getMessages(departureId).futureValue

      result mustEqual Right(departure)
    }

    "must return HttpResponse with an internal server error if there is a model mismatch" in {
      val connector = app.injector.instanceOf[DepartureConnector]
      val departure = Departure(1, "/movements/departures/1", "/movements/departures/1/messages", Some("MRN"), "ref", "status", LocalDateTime.now, LocalDateTime.now)

      val response = ResponseDeparture(departure)
      server.stubFor(
        get(
          urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1/messages")
        ).willReturn(aResponse().withStatus(OK)
          .withBody(Json.toJson(response).toString())))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.getMessages(departureId).futureValue

      result.isLeft mustEqual true
      result.left.map { x => x.status mustEqual INTERNAL_SERVER_ERROR }
    }

    "must return HttpResponse with a not found if not found" in {
      val connector = app.injector.instanceOf[DepartureConnector]
      server.stubFor(
        get(
          urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1/messages")
        ).willReturn(aResponse().withStatus(NOT_FOUND)))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.getMessages(departureId).futureValue

      result.isLeft mustEqual true
      result.left.map { x => x.status mustEqual NOT_FOUND }
    }

    "must return HttpResponse with a bad request if there is a bad request" in {
      val connector = app.injector.instanceOf[DepartureConnector]
      server.stubFor(
        get(
          urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1/messages")
        ).willReturn(aResponse().withStatus(BAD_REQUEST)))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.getMessages(departureId).futureValue

      result.isLeft mustEqual true
      result.left.map { x => x.status mustEqual BAD_REQUEST }
    }

    "must return HttpResponse with an internal server if there is an internal server error" in {
      val connector = app.injector.instanceOf[DepartureConnector]
      server.stubFor(
        get(
          urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1/messages")
        ).willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR)))

      implicit val hc = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result = connector.getMessages(departureId).futureValue

      result.isLeft mustEqual true
      result.left.map { x => x.status mustEqual INTERNAL_SERVER_ERROR }
    }
  }

  override protected def portConfigKey: String = "microservice.services.transits-movements-trader-at-departure.port"
}
