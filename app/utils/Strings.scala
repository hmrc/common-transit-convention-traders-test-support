/*
 * Copyright 2022 HM Revenue & Customs
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

import org.scalacheck.Gen
import scala.language.postfixOps
import scala.util.Random

object Strings {

  def between1And9: Int = Gen.choose(1, 9).sample.getOrElse(1)

  def alphanumeric(length: Int): String =
    Random.alphanumeric take length mkString

  def alphanumeric(lengthStart: Int, lengthEnd: Int): String = {
    val length = Gen.choose(lengthStart, lengthEnd).sample.getOrElse(lengthStart)
    Random.alphanumeric take length mkString
  }

  def zeroOrOne() = Gen.oneOf(0, 1).sample.getOrElse(0).toString

  def numeric(length: Int): String =
    Random.alphanumeric.filter(_.isDigit).take(length).mkString

  def numeric(lengthStart: Int, lengthEnd: Int): String = {
    val length = Gen.choose(lengthStart, lengthEnd).sample.getOrElse(lengthStart)
    Random.alphanumeric.filter(_.isDigit).take(length).mkString
  }

  def alpha(maxLen: Int, minLen: Int = 1) =
    (for {
      len <- Gen.choose(minLen, maxLen)
      str <- Gen.stringOfN(len, Gen.alphaChar)
    } yield str).sample.get

  def num(len: Int) =
    Gen.stringOfN(len, Gen.numChar).sample.get

  def alphanumericCapital(maxLen: Int, minLen: Int = 1) =
    (for {
      len <- Gen.choose(minLen, maxLen)
      str <- Gen.stringOfN(len, Gen.alphaChar)
    } yield str.toUpperCase).sample.get

  private def num1(len: Int) =
    (for {
      initChar  <- Gen.choose(1, 9).map(_.toString)
      restChars <- Gen.stringOfN(len - 1, Gen.numChar)
    } yield initChar + restChars).sample.get

  def decimalNumber(totalDigits: Int, fractionDigits: Int) =
    s"${num1(totalDigits - fractionDigits)}.${num(fractionDigits)}"

  def decimalMax12(): String =
    Seq.fill(11)(between1And9).mkString

  def numeric8(): String = {
    val first  = Gen.choose(1, 2).sample.getOrElse(1)
    val second = Gen.choose(0, 9).sample.getOrElse(9)

    Seq(
      first.toString,
      second.toString,
      first.toString,
      second.toString,
      "0",
      between1And9.toString,
      "0",
      between1And9.toString
    ).mkString
  }

  def mrn(): String =
    //pattern value="([2][4-9]|[3-9][0-9])[A-Z]{2}[A-Z0-9]{12}[J-M][0-9]"
    Seq[String](
      "2",
      Gen.choose(4, 9).sample.getOrElse(4).toString,
      alpha(2),
      alphanumeric(12),
      Gen.oneOf(Seq("J", "K", "L", "M")).sample.getOrElse("J"),
      numeric(1)
    ).mkString.toUpperCase

  def grn(): String =
    //[0-9]{2}[A-Z]{2}[A-Z0-9]{12}[0-9]([A-Z][0-9]{6})?
    Seq[String](
      numeric(2),
      alpha(2),
      alphanumeric(12),
      numeric(1)
    ).mkString.toUpperCase

  def referenceNumber(): String =
    // [A-Z]{2}[A-Z0-9]{6}
    Seq(alpha(2), alphanumeric(6)).mkString.toUpperCase

  def alpha(length: Int): String =
    Random.alphanumeric.filter(_.isLetter).take(length).mkString

}
