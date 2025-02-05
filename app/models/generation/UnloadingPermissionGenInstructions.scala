/*
 * Copyright 2023 HM Revenue & Customs
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

package models.generation

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads
import play.api.libs.json.__

case class UnloadingPermissionGenInstructions(goodsCount: Int, productCount: Int, specialMentionsCount: Int, sealsCount: Int) extends GenInstructions {}

object UnloadingPermissionGenInstructions {

  def validate(i: UnloadingPermissionGenInstructions): Either[String, UnloadingPermissionGenInstructions] =
    (i.goodsCount, i.productCount, i.specialMentionsCount, i.sealsCount) match {
      case (gc, _, _, _) if gc > 999  => Left("Too many goods requested, max 999")
      case (_, pc, _, _) if pc > 99   => Left("Too many products requested, max 99")
      case (_, _, smc, _) if smc > 99 => Left("Too many special mentions requested, max 99")
      case (_, _, _, sc) if sc > 9999 => Left("Too many seals requested, max 9999")
      case (_, _, _, _)               => Right(i)
    }

  implicit val readsUnloadingPermissionGenInstructions: Reads[UnloadingPermissionGenInstructions] =
    (
      (__ \ "message" \ "goodsCount").readWithDefault[Int](1) and
        (__ \ "message" \ "productCount").readWithDefault[Int](1) and
        (__ \ "message" \ "specialMentionsCount").readWithDefault[Int](1) and
        (__ \ "message" \ "sealsCount").readWithDefault[Int](1)
    )(UnloadingPermissionGenInstructions.apply)
}
