package repositories

import cats.data.NonEmptyList
import generators.ModelGenerators
import models.MessageStatus.SubmissionPending
import models.{Departure, DepartureId, MessageWithStatus, MongoDateTimeFormats}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalactic.source
import org.scalatest._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.exceptions.{StackDepthException, TestFailedException}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag

class DepartureRepositorySpec extends AnyFreeSpec with TryValues with OptionValues with ModelGenerators with Matchers with ScalaFutures with MongoSuite with GuiceOneAppPerSuite with IntegrationPatience  with MongoDateTimeFormats {

  private val service = app.injector.instanceOf[DepartureRepository]

  def typeMatchOnTestValue[A, B](testValue: A)(test: B => Unit)(implicit bClassTag: ClassTag[B]) = testValue match {
    case result: B => test(result)
    case failedResult => throw new TestFailedException((_: StackDepthException) => Some(s"Test for ${bClassTag.runtimeClass}, but got a ${failedResult.getClass}"), None, implicitly[source.Position])
  }

  val departureWithOneMessage: Gen[Departure] = for {
    departure <- arbitrary[Departure]
    message <- arbitrary[MessageWithStatus]
  } yield departure.copy(messages = NonEmptyList.one(message.copy(status = SubmissionPending)))


  "DepartureRepository" - {

    "insert" - {
      "must persist Departure within mongoDB" in {
        database.flatMap(_.drop()).futureValue

        val departure = arbitrary[Departure].sample.value

        service.insert(departure).futureValue

        val selector = Json.obj("_id" -> departure.departureId)

        val result = database.flatMap {
          result =>
            result.collection[JSONCollection](DepartureRepository.collectionName).find(selector, None).one[Departure]
        }

        whenReady(result) { r =>
          r.value mustBe departure
        }
      }
    }

    "get(departureId: DepartureId)" - {
      "must get an departure when it exists" in {
        database.flatMap(_.drop()).futureValue

        val departure = arbitrary[Departure].sample.value

        service.insert(departure).futureValue
        val result = service.get(departure.departureId)

        whenReady(result) { r =>
          r.value mustEqual departure
        }
      }

      "must return None when an departure does not exist" in {
        database.flatMap(_.drop()).futureValue

        val departure = arbitrary[Departure].sample.value copy (departureId = DepartureId(1))

        service.insert(departure).futureValue
        val result = service.get(DepartureId(2))

        whenReady(result) { r =>
          r.isDefined mustBe false
        }
      }
    }
  }
}
