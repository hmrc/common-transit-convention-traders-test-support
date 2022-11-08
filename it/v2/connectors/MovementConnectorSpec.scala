package v2.connectors

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
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
import play.api.http.Status.NOT_FOUND
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.mvc.Request
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.UpstreamErrorResponse
import utils.WiremockSuite
import v2.generators.ItGenerators
import v2.models.EORINumber
import v2.models.Message
import v2.models.MessageId
import v2.models.MessageType
import v2.models.Movement
import v2.models.MovementId
import v2.models.MovementReferenceNumber
import v2.models.MovementType

import java.time.OffsetDateTime
import scala.concurrent.ExecutionContext.Implicits.global

class MovementConnectorSpec extends AnyFreeSpec with Matchers with WiremockSuite with ItGenerators with ScalaCheckDrivenPropertyChecks with ScalaFutures with OptionValues {

  lazy val httpClient = app.injector.instanceOf[HttpClient]
  lazy val appConfig = app.injector.instanceOf[AppConfig]

  "getMovement" - {

    def getMovementUri(movementType: MovementType, eori: EORINumber, movementId: MovementId)
      = s"/transit-movements/traders/${eori.value}/movements/${movementType.urlFragment}/${movementId.value}"

    "a successful retrieval should result in a Movement object being returned" in forAll(
      arbitrary[MovementType],
      arbitrary[EORINumber],
      arbitrary[MovementId],
      Gen.option(arbitrary[MovementReferenceNumber]),
      arbitrary[OffsetDateTime]
    ) {
      (movementType, eori, movementId, maybeMrn, offsetDateTime) =>
        implicit val hc: HeaderCarrier = HeaderCarrier()
        implicit val request: Request[AnyContent] = FakeRequest("GET", "/")

        server.stubFor(
          get(
            urlEqualTo(getMovementUri(movementType, eori, movementId))
          )
            .willReturn(
              aResponse()
                .withBody(Json.stringify(
                  Json.obj(
                    "_id" -> movementId.value,
                    "enrollmentEORINumber" -> eori.value,
                    "movementEORINumber" -> eori.value,
                    "movementReferenceNumber" -> maybeMrn,
                    "created" -> offsetDateTime,
                    "updated" -> offsetDateTime
                  )
                ))
                .withStatus(OK))
        )
        val sut = new MovementConnector(httpClient, appConfig)
        whenReady(sut.getMovement(movementType, eori, movementId)) {
          r => r mustBe Movement(
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
        implicit val request: Request[AnyContent] = FakeRequest("GET", "/")

        server.stubFor(
          get(
            urlEqualTo(getMovementUri(movementType, eori, movementId))
          )
            .willReturn(aResponse().withStatus(NOT_FOUND))
        )
        val sut = new MovementConnector(httpClient, appConfig)
        val result = sut
          .getMovement(movementType, eori, movementId)
          .map(_ => fail("Should not have succeeded"))
          .recover {
            case UpstreamErrorResponse(_, NOT_FOUND, _, _) => ()
          }
        whenReady(result) {
          r => // success means we got the response we were looking for
        }
    }

  }

  "getMessage" - {

    def getMessageUri(movementType: MovementType, eori: EORINumber, movementId: MovementId, messageId: MessageId)
      = s"/transit-movements/traders/${eori.value}/movements/${movementType.urlFragment}/${movementId.value}/messages/${messageId.value}"

    "a successful retrieval should result in a Movement object being returned" in forAll(
      arbitrary[MovementType],
      arbitrary[EORINumber],
      arbitrary[MovementId],
      arbitrary[MessageId],
      arbitrary[MessageType],
      arbitrary[OffsetDateTime]
    ) {
      (movementType, eori, movementId, messageId, messageType, offsetDateTime) =>
        val maybeBody = Gen.option(Gen.alphaNumStr).sample.value // we can only do six generators above, so here's the seventh
        implicit val hc: HeaderCarrier = HeaderCarrier()
        implicit val request: Request[AnyContent] = FakeRequest("GET", "/")

        server.stubFor(
          get(
            urlEqualTo(getMessageUri(movementType, eori, movementId, messageId))
          )
            .willReturn(
              aResponse()
                .withBody(Json.stringify(
                  Json.obj(
                    "id" -> messageId.value,
                    "received" -> offsetDateTime,
                    "messageType" -> messageType,
                    "body" -> maybeBody
                  )
                ))
                .withStatus(OK))
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
        implicit val request: Request[AnyContent] = FakeRequest("GET", "/")

        server.stubFor(
          get(
            urlEqualTo(getMessageUri(movementType, eori, movementId, messageId))
          )
            .willReturn(aResponse().withStatus(NOT_FOUND))
        )
        val sut = new MovementConnector(httpClient, appConfig)
        val result = sut
          .getMessage(movementType, eori, movementId, messageId)
          .map(_ => fail("Should not have succeeded"))
          .recover {
            case UpstreamErrorResponse(_, NOT_FOUND, _, _) => ()
          }
        whenReady(result) {
          r => // success means we got the response we were looking for
        }
    }

  }


  override protected def portConfigKey: String = "microservice.services.transit-movements.port"
}
