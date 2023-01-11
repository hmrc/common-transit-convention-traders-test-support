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

package v2.controllers

import base.SpecBase
import cats.data.EitherT
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.HeaderNames
import play.api.http.MimeTypes
import play.api.http.Status.CREATED
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.http.Status.NOT_FOUND
import play.api.http.Status.NOT_IMPLEMENTED
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import play.api.test.Helpers.defaultAwaitTimeout
import play.api.test.Helpers.status
import play.api.test.Helpers.stubControllerComponents
import v2.base.TestActorSystem
import v2.controllers.actions.ValidateMessageTypeAction
import v2.fakes.controllers.actions.FakeAuthAction
import v2.fakes.controllers.actions.FakeMessageRequestAction
import v2.fakes.controllers.actions.FakeValidateMessageTypeActionProvider
import v2.generators.ModelGenerators
import v2.models._
import v2.models.errors.MessageGenerationError
import v2.models.errors.PersistenceError
import v2.models.errors.RouterError
import v2.services.InboundRouterService
import v2.services.MessageGenerationService
import v2.services.MovementPersistenceService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.xml.NodeSeq

class V2TestMessagesControllerSpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators with BeforeAndAfterEach with TestActorSystem {

  val exampleRequest: JsValue = Json.parse(
    """{
        |     "message": {
        |         "messageType": "IE928"
        |     }
        | }""".stripMargin
  )

  "V2TestMessagesController" - {
    "POST" - {
      "must send a test message to the persistence backend and return Created if successful" in forAll(
        arbitrary[MovementType],
        arbitrary[Movement],
        arbitrary[Message]
      ) {
        (movementType, movement, message) =>
          val mockMovementPersistenceService = mock[MovementPersistenceService]
          val mockInboundRouterService       = mock[InboundRouterService]
          val mockMessageGenerationService   = mock[MessageGenerationService]

          val validateAction         = new ValidateMessageTypeAction(Seq(message.messageType))
          val validateActionProvider = FakeValidateMessageTypeActionProvider(validateAction)

          when(
            mockMovementPersistenceService
              .getMovement(any[MovementType], any[String].asInstanceOf[EORINumber], any[String].asInstanceOf[MovementId])(any(), any(), any())
          ).thenReturn(EitherT[Future, PersistenceError, Movement](Future.successful(Right(movement))))

          when(mockInboundRouterService.post(any[MessageType], XMLMessage(any[NodeSeq]), any[CorrelationId])(any(), any()))
            .thenReturn(EitherT[Future, RouterError, MessageId](Future.successful(Right(message.id))))

          when(
            mockMovementPersistenceService
              .getMessage(any[MovementType], any[String].asInstanceOf[EORINumber], any[String].asInstanceOf[MovementId], any[String].asInstanceOf[MessageId])(
                any(),
                any(),
                any()
              )
          ).thenReturn(EitherT[Future, PersistenceError, Message](Future.successful(Right(message))))

          when(mockMessageGenerationService.generateMessage(any[MessageType], any[MovementType], MovementId(anyString()))(any())).thenAnswer(
            _ => EitherT.rightT[Future, XMLMessage](XMLMessage(<test></test>))
          )

          val sut = new TestMessagesController(
            stubControllerComponents(),
            FakeAuthAction,
            mockMovementPersistenceService,
            mockInboundRouterService,
            FakeMessageRequestAction(message.messageType),
            validateActionProvider,
            mockMessageGenerationService
          )

          val result =
            sut.injectEISResponse(movementType, movement._id)(
              FakeRequest("POST", "/", FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> MimeTypes.JSON)), Json.obj())
            )

          status(result) mustEqual CREATED
      }

      "must send a test message to the departures backend and return Not found if no matching departure" in forAll(
        arbitrary[MovementType],
        arbitrary[Movement],
        arbitrary[Message]
      ) {
        (movementType, movement, message) =>
          val mockMovementPersistenceService = mock[MovementPersistenceService]
          val mockInboundRouterService       = mock[InboundRouterService]
          val mockMessageGenerationService   = mock[MessageGenerationService]

          val validateAction         = new ValidateMessageTypeAction(Seq(message.messageType))
          val validateActionProvider = FakeValidateMessageTypeActionProvider(validateAction)

          when(
            mockMovementPersistenceService
              .getMovement(any[MovementType], any[String].asInstanceOf[EORINumber], any[String].asInstanceOf[MovementId])(any(), any(), any())
          ).thenAnswer(
            _ => EitherT.leftT[Future, PersistenceError](PersistenceError.MovementNotFound(movementType, movement._id))
          )

          val sut = new TestMessagesController(
            stubControllerComponents(),
            FakeAuthAction,
            mockMovementPersistenceService,
            mockInboundRouterService,
            FakeMessageRequestAction(message.messageType),
            validateActionProvider,
            mockMessageGenerationService
          )

          val result =
            sut.injectEISResponse(movementType, movement._id)(
              FakeRequest("POST", "/", FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> MimeTypes.JSON)), Json.obj())
            )
          status(result) mustEqual NOT_FOUND
      }

      "must return an error if the message type cannot be found" in forAll(arbitrary[MovementType], arbitrary[Movement], arbitrary[MessageType]) {
        (movementType, movement, messageType) =>
          val mockMovementPersistenceService = mock[MovementPersistenceService]
          val mockInboundRouterService       = mock[InboundRouterService]
          val mockMessageGenerationService   = mock[MessageGenerationService]

          val validateAction         = new ValidateMessageTypeAction(Seq(messageType))
          val validateActionProvider = FakeValidateMessageTypeActionProvider(validateAction)

          when(
            mockMovementPersistenceService
              .getMovement(eqTo(movementType), any[String].asInstanceOf[EORINumber], any[String].asInstanceOf[MovementId])(any(), any(), any())
          ).thenReturn(EitherT[Future, PersistenceError, Movement](Future.successful(Right(movement))))

          when(mockMessageGenerationService.generateMessage(any[MessageType], any[MovementType], MovementId(anyString()))(any())).thenAnswer(
            _ => EitherT.leftT[Future, MessageGenerationError](MessageGenerationError.MessageTypeNotSupported(messageType))
          )

          val sut = new TestMessagesController(
            stubControllerComponents(),
            FakeAuthAction,
            mockMovementPersistenceService,
            mockInboundRouterService,
            FakeMessageRequestAction(messageType),
            validateActionProvider,
            mockMessageGenerationService
          )

          val result =
            sut.injectEISResponse(movementType, movement._id)(
              FakeRequest("POST", "/", FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> MimeTypes.JSON)), Json.obj())
            )
          status(result) mustEqual NOT_IMPLEMENTED
      }

      "must send a test message to the departures backend and return internal server error if there is a inbound router error" in forAll(
        arbitrary[MovementType],
        arbitrary[Movement],
        arbitrary[Message]
      ) {
        (movementType, movement, message) =>
          val mockMovementPersistenceService = mock[MovementPersistenceService]
          val mockInboundRouterService       = mock[InboundRouterService]
          val mockMessageGenerationService   = mock[MessageGenerationService]

          val validateAction         = new ValidateMessageTypeAction(Seq(message.messageType))
          val validateActionProvider = FakeValidateMessageTypeActionProvider(validateAction)

          when(
            mockMovementPersistenceService
              .getMovement(eqTo(movementType), any[String].asInstanceOf[EORINumber], any[String].asInstanceOf[MovementId])(any(), any(), any())
          ).thenReturn(EitherT[Future, PersistenceError, Movement](Future.successful(Right(movement))))

          when(mockMessageGenerationService.generateMessage(any[MessageType], any[MovementType], MovementId(anyString()))(any())).thenAnswer(
            _ => EitherT.rightT[Future, XMLMessage](XMLMessage(<test></test>))
          )

          when(mockInboundRouterService.post(any[MessageType], XMLMessage(any[NodeSeq]), any[CorrelationId])(any(), any()))
            .thenReturn(EitherT[Future, RouterError, MessageId](Future.successful(Left(RouterError.Unexpected()))))

          val sut = new TestMessagesController(
            stubControllerComponents(),
            FakeAuthAction,
            mockMovementPersistenceService,
            mockInboundRouterService,
            FakeMessageRequestAction(message.messageType),
            validateActionProvider,
            mockMessageGenerationService
          )

          val result =
            sut.injectEISResponse(movementType, movement._id)(
              FakeRequest("POST", "/", FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> MimeTypes.JSON)), Json.obj())
            )
          status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }
  }
}
