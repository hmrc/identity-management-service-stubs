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

import com.google.inject.Singleton
import play.api.Logging
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.identitymanagementservicestubs.controllers.ClientsController.{CLIENT_ID, SECRET}
import uk.gov.hmrc.identitymanagementservicestubs.models.{Client, ClientResponse}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

@Singleton
class ClientsController @Inject()(cc: ControllerComponents)
  extends BackendController(cc) with Logging {

  def createClient(): Action[JsValue] = Action(parse.json) {
    implicit request =>
      request.body.validate[Client] match {
        case JsSuccess(client, _) =>
          val clientResponse = ClientResponse(CLIENT_ID, SECRET)
          logger.info(s"Creating client $client returning $clientResponse")
          Ok(Json.toJson(clientResponse))
        case e: JsError =>
          logger.warn(s"Error parsing request body: ${JsError.toJson(e)}")
          BadRequest
      }
  }

  def deleteClient(id: String): Action[AnyContent] = Action {
    logger.info(s"Deleting client $id")
    Ok
  }

  def addClientScope(id: String, clientScopeId: String): Action[AnyContent] = Action {
    logger.info(s"Adding client scope $id $clientScopeId")
    Ok
  }

  def deleteClientScope(id: String, clientScopeId: String): Action[AnyContent] = Action {
    logger.info(s"Deleting client scope $id $clientScopeId")
    Ok
  }

}

object ClientsController {

  val CLIENT_ID: String = "CLIENTID123"
  val SECRET: String = "SECRET123"

}
