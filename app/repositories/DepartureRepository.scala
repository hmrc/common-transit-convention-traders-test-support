/*
 * Copyright 2020 HM Revenue & Customs
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

package repositories

import config.AppConfig
import javax.inject.Inject
import models.Departure
import models.DepartureId
import models.MongoDateTimeFormats
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONSerializationPack
import reactivemongo.api.indexes.Index.Aux
import reactivemongo.api.indexes.IndexType
import reactivemongo.play.json.collection.JSONCollection
import utils.IndexUtils
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DepartureRepository @Inject()(mongo: ReactiveMongoApi, appConfig: AppConfig)(implicit ec: ExecutionContext) extends MongoDateTimeFormats {

  private val index: Aux[BSONSerializationPack.type] = IndexUtils.index(
    key = Seq("eoriNumber" -> IndexType.Ascending),
    name = Some("eori-number-index")
  )

  private val cacheTtl = appConfig.cacheTtl

  private val lastUpdatedIndex: Aux[BSONSerializationPack.type] = IndexUtils.index(
    key = Seq("lastUpdated" -> IndexType.Ascending),
    name = Some("last-updated-index"),
    options = BSONDocument("expireAfterSeconds" -> cacheTtl)
  )

  val started: Future[Unit] = {
    collection
      .flatMap {
        jsonCollection =>
          for {
            _   <- jsonCollection.indexesManager.ensure(index)
            res <- jsonCollection.indexesManager.ensure(lastUpdatedIndex)
          } yield res
      }
      .map(_ => ())
  }

  private val collectionName = DepartureRepository.collectionName

  private def collection: Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](collectionName))

  def insert(departure: Departure): Future[Unit] =
    collection.flatMap {
      _.insert(false)
        .one(Json.toJsObject(departure))
        .map(_ => ())
    }

  def get(departureId: DepartureId): Future[Option[Departure]] = {

    val selector = Json.obj(
      "_id" -> departureId
    )

    collection.flatMap {
      _.find(selector, None)
        .one[Departure]
    }
  }
}

object DepartureRepository {
  val collectionName = "departures"
}
