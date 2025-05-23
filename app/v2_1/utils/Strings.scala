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

package v2_1.utils

import org.scalacheck.Gen

import scala.language.postfixOps
import scala.util.Random

object Strings {

  def between1And9: Int = Gen.choose(1, 9).sample.getOrElse(1)

  def between1And99999: Int = Gen.choose(1, 99999).sample.getOrElse(1)

  def alphanumeric(length: Int): String =
    Random.alphanumeric take length mkString

  def alphanumeric(lengthStart: Int, lengthEnd: Int): String = {
    val length = Gen.choose(lengthStart, lengthEnd).sample.getOrElse(lengthStart)
    Random.alphanumeric take length mkString
  }

  def zeroOrOne(): String = Gen.oneOf(0, 1).sample.getOrElse(0).toString

  def numeric(length: Int): String =
    Random.alphanumeric.filter(_.isDigit).take(length).mkString

  def numeric(lengthStart: Int, lengthEnd: Int): String = {
    val length = Gen.choose(lengthStart, lengthEnd).sample.getOrElse(lengthStart)
    Random.alphanumeric.filter(_.isDigit).take(length).mkString
  }

  def numericNonZeroStart(lengthStart: Int, lengthEnd: Int): String = {
    val first = Gen.choose(1, 9).map(_.toString).sample.getOrElse("1")
    val rest  = numeric(lengthStart, lengthEnd - 1)
    first + rest
  }

  def alpha(maxLen: Int, minLen: Int = 1): String =
    (for {
      len <- Gen.choose(minLen, maxLen)
      str <- Gen.stringOfN(len, Gen.alphaChar)
    } yield str).sample.get

  def num(len: Int): String =
    Gen.stringOfN(len, Gen.numChar).sample.get

  def alphanumericCapital(maxLen: Int, minLen: Int = 1): String =
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

  def mrn(): String =
    Seq[String](
      "2",
      Gen.choose(4, 9).sample.getOrElse(4).toString,
      alpha(2),
      alphanumeric(12),
      Gen.oneOf(Seq("J", "K", "L", "M")).sample.getOrElse("J"),
      numeric(1)
    ).mkString.toUpperCase

  def grn(): String =
    Seq[String](
      numeric(2),
      alpha(2),
      alphanumeric(12),
      numeric(1)
    ).mkString.toUpperCase

  def referenceNumber(): String =
    Seq(alpha(2), alphanumeric(6)).mkString.toUpperCase

  def alpha(length: Int): String =
    Random.alphanumeric.filter(_.isLetter).take(length).mkString

  def declarationGoodsItemNumber(): String =
    Seq(between1And9, between1And9).mkString

  def country(): String = {
    val values = List(
      "AD",
      "AT",
      "BE",
      "BG",
      "CH",
      "CY",
      "CZ",
      "DE",
      "DK",
      "EE",
      "ES",
      "FI",
      "FR",
      "GB",
      "GR",
      "HR",
      "HU",
      "IE",
      "IS",
      "IT",
      "LT",
      "LU",
      "LV",
      "MK",
      "MT",
      "NL",
      "NO",
      "PL",
      "PT",
      "RO",
      "RS",
      "SE",
      "SI",
      "SK",
      "SM",
      "TR",
      "XI"
    )
    Random.shuffle(values).head
  }

}
