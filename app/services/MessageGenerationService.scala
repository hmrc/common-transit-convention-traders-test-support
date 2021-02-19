package services

import com.google.inject.Inject
import controllers.actions.MessageRequest
import play.api.libs.json.JsValue

class MessageGenerationService @Inject() () {

  def generateMessage(request: MessageRequest[JsValue])

}

