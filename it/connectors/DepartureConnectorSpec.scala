package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models._
import models.domain.MovementMessage
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.Status.ACCEPTED
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import utils.WiremockSuite

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global

class DepartureConnectorSpec extends AnyFreeSpec
  with Matchers
  with WiremockSuite
  with ScalaFutures
  with IntegrationPatience
  with ScalaCheckPropertyChecks
  with OptionValues {

  private val departureId = new DepartureId(1)

  implicit val hc = HeaderCarrier()
  implicit val requestHeader = FakeRequest()

  "DepartureConnector" - {
    "getMessages" - {
      "must return Departure when departure is found" in {

        val channel: ChannelType = Gen.oneOf(ChannelType.values).sample.get
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

        val result = connector.getMessages(departureId, channel).futureValue

        result mustEqual Right(departure)
      }

      "must return HttpResponse with an internal server error if there is a model mismatch" in {

        val channel: ChannelType = Gen.oneOf(ChannelType.values).sample.get
        val connector = app.injector.instanceOf[DepartureConnector]
        val departure = Departure(1, "/movements/departures/1", "/movements/departures/1/messages", Some("MRN"), "ref", "status", LocalDateTime.now, LocalDateTime.now)

        val response = ResponseDeparture(departure)
        server.stubFor(
          get(
            urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1/messages")
          ).willReturn(aResponse().withStatus(OK)
            .withBody(Json.toJson(response).toString())))

         val result = connector.getMessages(departureId, channel).futureValue

        result.isLeft mustEqual true
        result.left.map { x => x.status mustEqual INTERNAL_SERVER_ERROR }
      }

      "must return HttpResponse with a not found if not found" in {

        val channel: ChannelType = Gen.oneOf(ChannelType.values).sample.get
        val connector = app.injector.instanceOf[DepartureConnector]
        server.stubFor(
          get(
            urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1/messages")
          ).willReturn(aResponse().withStatus(NOT_FOUND)))

        val result = connector.getMessages(departureId, channel).futureValue

        result.isLeft mustEqual true
        result.left.map { x => x.status mustEqual NOT_FOUND }
      }

      "must return HttpResponse with a bad request if there is a bad request" in {

        val channel: ChannelType = Gen.oneOf(ChannelType.values).sample.get
        val connector = app.injector.instanceOf[DepartureConnector]
        server.stubFor(
          get(
            urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1/messages")
          ).willReturn(aResponse().withStatus(BAD_REQUEST)))

        val result = connector.getMessages(departureId, channel).futureValue

        result.isLeft mustEqual true
        result.left.map { x => x.status mustEqual BAD_REQUEST }
      }

      "must return HttpResponse with an internal server if there is an internal server error" in {

        val channel: ChannelType = Gen.oneOf(ChannelType.values).sample.get
        val connector = app.injector.instanceOf[DepartureConnector]
        server.stubFor(
          get(
            urlEqualTo("/transits-movements-trader-at-departure/movements/departures/1/messages")
          ).willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR)))

        val result = connector.getMessages(departureId, channel).futureValue

        result.isLeft mustEqual true
        result.left.map { x => x.status mustEqual INTERNAL_SERVER_ERROR }
      }
    }

    "createDeclarationMessage" - {
      "must return status as OK for submission of valid movement" in {
        val connector = app.injector.instanceOf[DepartureConnector]
        val channelType = Gen.oneOf(ChannelType.values).sample.value

        stubPostResponse("/transits-movements-trader-at-departure/movements/departures/",
          ACCEPTED, channelType)

        val result = connector.createDeclarationMessage(<xml>test</xml>, channelType).futureValue

        result.status mustEqual ACCEPTED
      }

      "must return an error status when an error response is returned" in {
        val connector = app.injector.instanceOf[DepartureConnector]
        forAll(Gen.chooseNum(400, 599)) {
          (errorResponseCode) =>
            val channelType = Gen.oneOf(ChannelType.values).sample.value
            stubPostResponse("/transits-movements-trader-at-departure/movements/departures/", errorResponseCode, channelType)
            val inputXml = <xml>test</xml>

            val result = connector.createDeclarationMessage(inputXml, channelType)
            result.futureValue.status mustBe errorResponseCode
        }
      }
    }
  }

  private def stubPostResponse(stubUrl: String, expectedStatus: Int, channelType: ChannelType): StubMapping = {
    server.stubFor(
      post(urlEqualTo(stubUrl))
        .withHeader("Channel", containing(channelType.toString))
        .willReturn(
          aResponse()
            .withStatus(expectedStatus)
        )
    )
  }

  override protected def portConfigKey: String = "microservice.services.transits-movements-trader-at-departure.port"
}

