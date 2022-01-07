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

package connectors

import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.HeaderNames
import play.api.http.MimeTypes
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import uk.gov.hmrc.http.Authorization
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.Authorization

class BaseConnectorSpec
    extends AnyFreeSpec
    with Matchers
    with GuiceOneAppPerSuite
    with OptionValues
    with ScalaFutures
    with MockitoSugar
    with BeforeAndAfterEach {

  class Harness extends BaseConnector {

    def enforceAuth(extraHeaders: Seq[(String, String)])(implicit requestHeader: RequestHeader, headerCarrier: HeaderCarrier): HeaderCarrier =
      super.enforceAuthHeaderCarrier(extraHeaders)
  }

  "BaseConnector" - {
    "enforceAuthHeaderCarrier must enforce auth" in {
      val harness = new Harness()

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest().withHeaders(HeaderNames.AUTHORIZATION -> "a5sesqerTyi135/")

      val result: HeaderCarrier = harness.enforceAuth(Seq.empty)

      result.authorization mustBe Some(Authorization("a5sesqerTyi135/"))
    }

    "enforceAuthHeaderCarrier must add empty auth header if no auth header supplied in request" in {
      val harness = new Harness()

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result: HeaderCarrier = harness.enforceAuth(Seq.empty)

      result.authorization mustBe Some(Authorization(""))
    }

    "enforceAuthHeaderCarrier must contain extra headers if supplied" in {
      val harness = new Harness()

      implicit val hc            = HeaderCarrier()
      implicit val requestHeader = FakeRequest()

      val result: HeaderCarrier = harness.enforceAuth(Seq(HeaderNames.CONTENT_TYPE -> MimeTypes.JSON))

      result.extraHeaders must contain(HeaderNames.CONTENT_TYPE -> MimeTypes.JSON)
    }
  }
}
