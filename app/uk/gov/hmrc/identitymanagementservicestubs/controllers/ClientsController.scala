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

package uk.gov.hmrc.identitymanagementservicestubs.controllers

import com.google.inject.Singleton
import play.api.Logging
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.identitymanagementservicestubs.controllers.auth.Authenticator
import uk.gov.hmrc.identitymanagementservicestubs.models.{Client, ClientScope, Identity}
import uk.gov.hmrc.identitymanagementservicestubs.services.IdentityService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClientsController @Inject()(
  cc: ControllerComponents,
  idmsService: IdentityService,
  authenticator: Authenticator
)(implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  def createClient(): Action[JsValue] = authenticator.async(parse.json) {
    implicit request =>
      request.body.validate[Client] match {
        case JsSuccess(client, _) =>
          idmsService.createIdentity(Identity(client)).map {
            case Some(clientResonse) => Created(Json.toJson(clientResonse))
            case None =>
              logger.info(s"Error creating new Identity object for application: ${client.applicationName}")
              InternalServerError
          }
        case e: JsError =>
          logger.info(s"Error parsing request body: ${JsError.toJson(e)}")
          Future.successful(BadRequest)
      }
  }

  def getClientSecret(id: String): Action[AnyContent] = authenticator.async {
    logger.info(s"Getting client id = $id")
    idmsService.getSecret(id).map {
      case Some(secret) => Ok(Json.toJson(secret))
      case None => NotFound
    }
  }

  def newClientSecret(id: String): Action[AnyContent] = authenticator.async {
    logger.info(s"Creating new client secret for client id = $id")
    idmsService.newSecret(id).map {
      case Some(secret) => Ok(Json.toJson(secret))
      case None => NotFound
    }
  }

  def deleteClient(id: String): Action[AnyContent] = authenticator.async {
    logger.info(s"Deleting client $id")
    idmsService.deleteIdentity(id).map {
      case Some(()) => Ok
      case None => NotFound
    }
  }

  def addClientScope(id: String, clientScopeId: String): Action[AnyContent] = authenticator.async {
    logger.info(s"Adding client scope $id $clientScopeId")
    idmsService.addClientScope(id, clientScopeId).map {
      case Some(_) => Ok
      case _ => NotFound
    }
  }

  def deleteClientScope(id: String, clientScopeId: String): Action[AnyContent] = authenticator.async {
    logger.info(s"Deleting client scope $id $clientScopeId")
    idmsService.deleteClientScope(id, clientScopeId).map {
      case Some(_) => Ok
      case _ => NotFound
    }
  }

  def fetchClientScopes(id: String): Action[AnyContent] = authenticator.async {
    logger.info(s"Fetching scopes for $id")
    idmsService.fetchIdentity(id).map {
      case Some(identity) => Ok(Json.toJson(identity.scopes.map(ClientScope(_))))
      case _ => NotFound
    }
  }

}
