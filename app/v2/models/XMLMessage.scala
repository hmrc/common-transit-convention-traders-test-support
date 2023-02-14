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

package v2.models

import scala.annotation.tailrec
import scala.xml.Elem
import scala.xml.Node
import scala.xml.NodeSeq
import scala.xml.TopScope

object XMLMessage {

  lazy val targetPrefix = "txd"

  private def resetElem(elem: Elem, prefix: String = null): Elem =
    elem.copy(prefix = prefix, scope = TopScope, child = resetNamespaceBindings(elem.child))

  private def resetElemNamespaceBindings(nodes: NodeSeq): NodeSeq = nodes match {
    case x: Elem => resetElem(x, targetPrefix)
    case _       => throw new IllegalArgumentException("The supplied node is not an Elem")
  }

  private def resetNamespaceBindings(nodes: Seq[Node]): Seq[Node] = {

    @tailrec
    def accumulate(nodes: Seq[Node], acc: Seq[Node]): Seq[Node] = nodes.headOption match {
      case Some(head: Elem) => accumulate(nodes.tail, acc :+ resetElem(head))
      case Some(head)       => accumulate(nodes.tail, acc :+ head)
      case None             => acc
    }

    accumulate(nodes, Seq.empty)
  }
}

case class XMLMessage(value: NodeSeq) extends AnyVal {

  def wrapped: WrappedXMLMessage = {
    val inside = XMLMessage.resetElemNamespaceBindings(value)
    WrappedXMLMessage(<n1:TraderChannelResponse xmlns:txd="http://ncts.dgtaxud.ec"
                                                xmlns:n1="http://www.hmrc.gov.uk/eis/ncts5/v1"
                                                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                                xsi:schemaLocation="http://www.hmrc.gov.uk/eis/ncts5/v1 EIS_WrapperV10_TraderChannelSubmission-51.8.xsd">{inside}</n1:TraderChannelResponse>)
  }
}

case class WrappedXMLMessage(value: NodeSeq) extends AnyVal
