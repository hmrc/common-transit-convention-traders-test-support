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

package controllers.actions

import com.google.inject.ImplementedBy
import com.google.inject.Inject
import config.Constants.EnrolmentIdKey
import config.Constants.EnrolmentKey
import play.api.Logging
import play.api.mvc.*
import play.api.mvc.Results.Forbidden
import play.api.mvc.Results.Unauthorized
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@ImplementedBy(classOf[AuthActionImpl])
trait AuthAction extends ActionBuilder[AuthRequest, AnyContent] with ActionFunction[Request, AuthRequest]

class AuthActionImpl @Inject() (
  override val authConnector: AuthConnector,
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends AuthAction
    with AuthorisedFunctions
    with Logging {

  private def getEnrolmentIdentifier(
    enrolments: Enrolments,
    enrolmentKey: String,
    enrolmentIdKey: String
  ): Option[String] =
    for {
      enrolment  <- enrolments.getEnrolment(enrolmentKey)
      identifier <- enrolment.getIdentifier(enrolmentIdKey)
    } yield identifier.value

  override def invokeBlock[A](request: Request[A], block: AuthRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    authorised(Enrolment(EnrolmentKey)).retrieve(Retrievals.authorisedEnrolments) {
      enrolments =>
        getEnrolmentIdentifier(
          enrolments,
          EnrolmentKey,
          EnrolmentIdKey
        ).map {
          eoriNumber =>
            block(AuthRequest(request, eoriNumber))
        }.getOrElse {
          Future.failed(InsufficientEnrolments(s"Unable to retrieve enrolment for $EnrolmentKey"))
        }
    }
  } recover {
    case e: InsufficientEnrolments =>
      logger.warn("Failed to authorise due to insufficient enrolments", e)
      Forbidden("Current user doesn't have a valid EORI enrolment.")
    case e: AuthorisationException =>
      logger.warn(s"Failed to authorise", e)
      Unauthorized
  }
}
