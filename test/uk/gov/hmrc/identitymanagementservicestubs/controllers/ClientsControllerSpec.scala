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

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.identitymanagementservicestubs.controllers.ClientsControllerSpec.buildApplication
import uk.gov.hmrc.identitymanagementservicestubs.models.{Client, ClientResponse}

class ClientsControllerSpec extends AnyFreeSpec with Matchers with OptionValues {

  "createClient" - {
    "must return Ok and a ClientResponse for a valid request" in {
      val client = Client(
        applicationName = "test-application-name",
        description = "test-description"
      )

      val expected = ClientResponse(ClientsController.CLIENT_ID, ClientsController.SECRET)

      val application = buildApplication()

      running(application) {
        val request = FakeRequest(
          POST,
          routes.ClientsController.createClient().url
        )
        .withHeaders(
          CONTENT_TYPE -> "application/json"
        )
        .withBody(Json.toJson(client))

        val result = route(application, request).value
        status(result) mustBe Status.OK
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

  def buildApplication(): Application = {
    new GuiceApplicationBuilder()
      .overrides(
        bind[ControllerComponents].toInstance(Helpers.stubControllerComponents()),
      )
      .build()
  }

}
