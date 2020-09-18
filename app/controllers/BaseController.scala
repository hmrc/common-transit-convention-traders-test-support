package controllers

import com.google.inject.Inject
import connectors.BaseConnector
import controllers.actions.AuthAction
import controllers.actions.GeneratedMessageRequest
import controllers.actions.ValidateArrivalMessageTypeAction
import controllers.actions.ValidateDepartureMessageTypeAction
import models.ArrivalId
import models.DepartureId
import models.ItemId
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import utils.ResponseHelper

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class BaseController @Inject()(cc: ControllerComponents,
                               connector: BaseConnector,
                               authAction: AuthAction,
                               validateDepartureMessageTypeAction: ValidateDepartureMessageTypeAction,
                               validateArrivalMessageTypeAction: ValidateArrivalMessageTypeAction)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with HttpErrorFunctions
    with ResponseHelper {

  def injectEISResponse(itemId: ItemId): Action[JsValue] = {
    val validationType = itemId match {
      case ArrivalId(_)   => validateArrivalMessageTypeAction
      case DepartureId(_) => validateDepartureMessageTypeAction
    }
    (authAction andThen validationType).async(parse.json) {
      implicit request: GeneratedMessageRequest[JsValue] =>
        connector
          .get(itemId)
          .flatMap {
            getResponse =>
              getResponse.status match {
                case status if is2xx(status) =>
                  connector
                    .post(request.testMessage.messageType, request.generatedMessage.toString(), itemId)
                    .map {
                      postResponse =>
                        postResponse.status match {
                          case status if is2xx(status) =>
                            Created
                          case _ =>
                            handleNon2xx(postResponse)
                        }
                    }
                    .recover {
                      case _ =>
                        InternalServerError
                    }
                case _ =>
                  Future.successful(handleNon2xx(getResponse))
              }
          }
          .recover {
            case _ =>
              InternalServerError
          }
    }
  }
}
