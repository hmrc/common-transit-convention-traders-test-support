package models

import play.api.libs.json.Format
import play.api.libs.json.JsError
import play.api.libs.json.JsNumber
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.mvc.PathBindable

import scala.util.Try

case class Id(index: Int)

object Id {
  implicit val formatsId: Format[Id] = new Format[Id] {
    override def reads(json: JsValue): JsResult[Id] = json match {
      case JsNumber(number) =>
        Try(number.toInt)
          .map(Id(_))
          .map(JsSuccess(_))
          .getOrElse(JsError("Error in converting JsNumber to an Int"))

      case e =>
        JsError(s"Error in deserialization of Json value to an Id, expected JsNumber got ${e.getClass}")
    }

    override def writes(o: Id): JsNumber = JsNumber(o.index)
  }

  implicit lazy val pathBindable: PathBindable[Id] = new PathBindable[Id] {
    override def bind(key: String, value: String): Either[String, Id] =
      implicitly[PathBindable[Int]].bind(key, value).right.map(Id(_))

    override def unbind(key: String, value: Id): String =
      value.index.toString
  }

}
