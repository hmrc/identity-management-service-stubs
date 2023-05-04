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

package uk.gov.hmrc.identitymanagementservicestubs.controllers.auth

import com.google.inject.Inject
import play.api.Logging
import play.api.mvc._
import uk.gov.hmrc.identitymanagementservicestubs.config.AppConfig

import java.util.Base64
import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex

class Authenticator @Inject()(
  appConfig: AppConfig,
  override val parser: BodyParsers.Default
)(implicit override val executionContext: ExecutionContext)
  extends ActionBuilder[Request, AnyContent] with ActionFilter[Request] with Logging {

  private val tokenPattern: Regex = "^Basic (.+)$".r
  private val credentialsPattern: Regex = "^(.+):(.+)$".r

  override protected def filter[A](request: Request[A]): Future[Option[Result]] = {
    Future.successful(
      request.headers.get("Authorization")
        .flatMap(extractToken)
        .flatMap(decodeCredentials) match {
          case Some(Credentials(appConfig.inboundClientId, appConfig.inboundSecret)) => None
          case _ => Some(Results.Unauthorized)
        }
    )
  }

  private def extractToken(header: String): Option[String] = {
    header match {
      case tokenPattern(token) => Some(token)
      case _ => None
    }
  }

  private def decodeCredentials(token: String): Option[Credentials] = {
    try {
      new String(Base64.getDecoder.decode(token)) match {
        case credentialsPattern(clientId, secret) =>
          logger.info(s"Request using Client Id $clientId")
          Some(Credentials(clientId, secret))
        case _ => None
      }
    }
    catch {
      case _: Throwable => None
    }
  }

  private case class Credentials(clientId: String, secret: String)

}
