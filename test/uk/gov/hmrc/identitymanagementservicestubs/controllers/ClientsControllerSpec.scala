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

package uk.gov.hmrc.identitymanagementservicestubs.controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import org.mockito.ArgumentMatchers.any
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{ControllerComponents, Request}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.identitymanagementservicestubs.controllers.ClientsControllerSpec.{buildApplication, buildFixture}
import uk.gov.hmrc.identitymanagementservicestubs.models.{Client, ClientResponse, Identity}
import org.mockito.MockitoSugar.mock
import org.mockito.{ArgumentMatchers, MockitoSugar}
import play.api.{Application => PlayApplication}
import uk.gov.hmrc.identitymanagementservicestubs.services.IdentityService

import scala.concurrent.Future

class ClientsControllerSpec extends AnyFreeSpec with Matchers with MockitoSugar with OptionValues {

  "createClient" - {
    "must return Created and a ClientResponse for a valid request" in {
      val fixture = buildFixture()
      running(fixture.application) {
        val client = Client(
          applicationName = "test-application-name",
          description = "test-description"
        )
        val json = Json.toJson(client)
        val request: Request[JsValue] = FakeRequest(POST, routes.ClientsController.createClient().url)
          .withHeaders(
            CONTENT_TYPE -> "application/json"
          ).withBody(json)

        val expected = ClientResponse("CLIENTID123", "SECRET123")

        when(fixture.idmsService.createIdentity(any[Identity]))
          .thenReturn(Future.successful(Some(expected)))

        val result = route(fixture.application, request).value
        status(result) mustBe Status.CREATED
        contentAsJson(result) mustBe Json.toJson(expected)
      }

    }

    "must return Bad Request for an invalid request" in {
      val application = buildApplication()

      running(application) {
        val request = FakeRequest(
          POST,
          routes.ClientsController.createClient().url
        )
        .withHeaders(
          CONTENT_TYPE -> "application/json"
        )
        .withBody(Json.parse("{}"))

        val result = route(application, request).value
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

  "deleteClient" - {
    "must return Ok" in {
      val application = buildApplication()

      running(application) {
        val request = FakeRequest(
          DELETE,
          routes.ClientsController.deleteClient("test-client-id").url
        )

        val result = route(application, request).value
        status(result) mustBe Status.OK
      }
    }
  }

  "addClientScope" - {
    "must return Ok" in {
      val application = buildApplication()

      running(application) {
        val request = FakeRequest(
          PUT,
          routes.ClientsController.addClientScope("test-client-id", "test-client-scope-id").url
        )

        val result = route(application, request).value
        status(result) mustBe Status.OK
      }
    }
  }

  "deleteClientScope" - {
    "must return Ok" in {
      val application = buildApplication()

      running(application) {
        val request = FakeRequest(
          DELETE,
          routes.ClientsController.addClientScope("test-client-id", "test-client-scope-id").url
        )

        val result = route(application, request).value
        status(result) mustBe Status.OK
      }
    }
  }

}

object ClientsControllerSpec {

  implicit val materializer: Materializer = Materializer(ActorSystem())

  case class Fixture(
                      application: PlayApplication,
                      idmsService: IdentityService
                    )

  def buildFixture(): Fixture = {
    val applicationsService = mock[IdentityService]

    val application = new GuiceApplicationBuilder()
      .overrides(
        bind[ControllerComponents].toInstance(Helpers.stubControllerComponents()),
        bind[IdentityService].toInstance(applicationsService)
      )
      .build()

    Fixture(application, applicationsService)
  }



  def buildApplication(): Application = {
    new GuiceApplicationBuilder()
      .overrides(
        bind[ControllerComponents].toInstance(Helpers.stubControllerComponents()),
      )
      .build()
  }

}
