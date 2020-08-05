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

package utils

import scala.util.Random

import org.scalacheck.Gen

object Strings {

  def alphanumeric(length: Int): String =
    Random.alphanumeric take length mkString

  def alphanumeric(lengthStart: Int, lengthEnd: Int): String = {
    val length = Gen.choose(lengthStart, lengthEnd).sample.getOrElse(lengthStart)
    Random.alphanumeric take length mkString
  }

  def numeric(length: Int): String =
    Random.alphanumeric.filter(_.isDigit).take(length).mkString

  def numeric(lengthStart: Int, lengthEnd: Int): String = {
    val length = Gen.choose(lengthStart, lengthEnd).sample.getOrElse(lengthStart)
    Random.alphanumeric.filter(_.isDigit).take(length).mkString
  }

  def numeric(lengthStart: Int, lengthEnd: Int, excludeChars: Seq[Int] = Seq()): String = {
    val length = Gen.choose(lengthStart, lengthEnd).sample.getOrElse(lengthStart)
    Random.alphanumeric.filter(x => (!excludeChars.contains(x)) && x.isDigit).take(length).mkString
  }

  def numeric8(): String = {
    val first  = Gen.choose(1, 2).sample.getOrElse(1)
    val second = Gen.choose(0, 9).sample.getOrElse(9)

    Seq(
      first.toString,
      second.toString,
      first.toString,
      second.toString,
      "0",
      Gen.choose(1, 9).sample.getOrElse(5).toString,
      "0",
      Gen.choose(1, 9).sample.getOrElse(7).toString,
    ).mkString
  }

  def alpha(length: Int): String =
    Random.alphanumeric.filter(_.isLetter).take(length).mkString
}
