//package controllers
//
//import connectors.BaseConnector
//import controllers.actions.GeneratedMessageRequest
//import javax.inject.Inject
//import models.ItemId
//import play.api.libs.json.JsValue
//import uk.gov.hmrc.http.{HeaderCarrier, HttpErrorFunctions, HttpResponse}
//import utils.ResponseHelper
//
//import scala.concurrent.ExecutionContext
//import scala.concurrent.Future
//
//class BaseController @Inject()(connector: BaseConnector)(implicit ec: ExecutionContext, implicit hc: HeaderCarrier) extends HttpErrorFunctions with ResponseHelper {
//
//  def func(getResponse: HttpResponse, request: GeneratedMessageRequest[JsValue], itemId: ItemId) =
//    getResponse.status match {
//      case status if is2xx(status) =>
//        connector
//          .post(request.testMessage.messageType, request.generatedMessage.toString(), itemId, "", "")
//          .map {
//            postResponse =>
//              postResponse.status match {
//                case status if is2xx(status) =>
//                  Created
//                case _ =>
//                  handleNon2xx(postResponse)
//              }
//          }
//          .recover {
//            case _ =>
//              InternalServerError
//          }
//      case _ =>
//        Future.successful(handleNon2xx(getResponse))
//    }
//}
