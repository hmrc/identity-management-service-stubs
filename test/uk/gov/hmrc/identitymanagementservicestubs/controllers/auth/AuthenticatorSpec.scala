/*
 * Copyright 2024 HM Revenue & Customs
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

import org.apache.pekko.stream.testkit.NoMaterializer
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Configuration
import play.api.mvc.{BodyParsers, Request, Result, Results}
import play.api.test.{FakeRequest, StubPlayBodyParsersFactory}
import uk.gov.hmrc.identitymanagementservicestubs.config.AppConfig

import java.util.Base64
import scala.concurrent.{ExecutionContext, Future}

class AuthenticatorSpec extends AsyncFreeSpec with Matchers {

  import AuthenticatorSpec._

  "Authenticator.filter" - {
    "must pass authentication when valid credentials are presented" in {
      val request = FakeRequest().withHeaders(("Authorization", buildHeader(testClientId, testSecret)))
      authenticator().filter(request).map {
        result =>
          result mustBe None
      }
    }

    "must fail authentication when invalid credentials are presented" in {
      val request = FakeRequest().withHeaders(("Authorization", buildHeader("invalid", "invalid")))
      authenticator().filter(request).map {
        result =>
          result mustBe Some(Results.Unauthorized)
      }
    }

    "must fail authentication when the clientId/secret token cannot be decoded" in {
      val request = FakeRequest().withHeaders(("Authorization", "Basic !@£$"))
      authenticator().filter(request).map {
        result =>
          result mustBe Some(Results.Unauthorized)
      }
    }

    "must fail authentication when the authorisation header does not include 'Basic'" in {
      val request = FakeRequest().withHeaders(("Authorization", "dGVzdC1jbGllbnQtaWQ6dGVzdC1zZWNyZXQ="))
      authenticator().filter(request).map {
        result =>
          result mustBe Some(Results.Unauthorized)
      }
    }

    "must fail authentication when the authorisation header is not present" in {
      val request = FakeRequest()
      authenticator().filter(request).map {
        result =>
          result mustBe Some(Results.Unauthorized)
      }
    }
  }

}

object AuthenticatorSpec extends StubPlayBodyParsersFactory {

  val testClientId: String = "test-client-id"
  val testSecret: String = "test-secret"

  private val bodyParser: BodyParsers.Default = new BodyParsers.Default(stubPlayBodyParsers(NoMaterializer))

  class TestAuthenticator(appConfig: AppConfig)(implicit ec: ExecutionContext) extends Authenticator(appConfig, bodyParser) {

    override def filter[A](request: Request[A]): Future[Option[Result]] = super.filter(request)

  }

  def authenticator()(implicit ec: ExecutionContext): TestAuthenticator = {
    val configuration = Configuration.from(
      Map(
        "appName" -> "test",
        "credentials.inbound.clientId" -> testClientId,
        "credentials.inbound.secret" -> testSecret
      )
    )

    new TestAuthenticator(new AppConfig(configuration))
  }

  def buildHeader(clientId: String, secret: String): String = {
    val encoded = Base64.getEncoder.encodeToString(s"$clientId:$secret".getBytes)
    s"Basic $encoded"
  }

}
