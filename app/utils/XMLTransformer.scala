/*
 * Copyright 2021 HM Revenue & Customs
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

import models.domain.MovementMessage

import scala.xml.Elem
import scala.xml.Node
import scala.xml.NodeSeq
import scala.xml.Text
import scala.xml.transform.RewriteRule
import scala.xml.transform.RuleTransformer

object XMLTransformer {

  def populateRefNumEPT1(message: NodeSeq, messageType: String, messages: Seq[MovementMessage]): NodeSeq =
    messages.filter(movementMessage => movementMessage.messageType == messageType).lastOption match {
      case Some(movementMessage: MovementMessage) =>
        val element = (movementMessage.message \\ "RefNumEPT1")
        if (element.nonEmpty)
          updateMessage(message, element.text)
        else
          message
      case None =>
        message
    }

  private def updateMessage(message: NodeSeq, value: String): NodeSeq = {
    val rewriteRule = new RewriteRule {
      override def transform(n: Node): NodeSeq = n match {
        case e: Elem if e.label.contains("RefNumEPT1") => e.copy(child = Text(value))
        case other                                     => other
      }
    }

    val rt = new RuleTransformer(rewriteRule)
    rt.transform(message)
  }
}
