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

package v2_1.controllers.stream

import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import play.api.libs.streams.Accumulator
import play.api.mvc.BaseControllerHelpers
import play.api.mvc.BodyParser

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

trait StreamingParsers {
  self: BaseControllerHelpers =>

  implicit val materializer: Materializer

  // TODO: do we choose a better thread pool, or make configurable?
  //  We have to be careful to not use Play's EC because we could accidentally starve the thread pool
  //  and cause errors for additional connections
  implicit val materializerExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))

  lazy val streamFromMemory: BodyParser[Source[ByteString, ?]] = BodyParser {
    _ =>
      Accumulator.source[ByteString].map(Right.apply)
  }
}
