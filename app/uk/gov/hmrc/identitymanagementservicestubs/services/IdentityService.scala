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

package uk.gov.hmrc.identitymanagementservicestubs.services

import com.google.inject.{Inject, Singleton}
import play.api.Logging
import uk.gov.hmrc.identitymanagementservicestubs.models.{ClientResponse, Identity, Secret}
import uk.gov.hmrc.identitymanagementservicestubs.repositories.IdentityRepository

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class IdentityService @Inject()(repository: IdentityRepository)(implicit ec: ExecutionContext)
extends Logging   {

  def createIdentity(identity: Identity): Future[Option[ClientResponse]] =
    repository.insert(identity).map(
      _.toClientResponse
    )

  def deleteIdentity(clientId: String): Future[Option[Unit]] =
    repository.delete(clientId)

  def getSecret(clientId: String): Future[Option[Secret]] =
    fetchIdentity(clientId).map {
      case Some(identity) => Some(Secret(identity.clientSecret))
      case _ => None
    }

  def newSecret(clientId: String): Future[Option[Secret]] =
    repository.find(clientId).flatMap {
      case Some(identity: Identity) =>
        repository
          .update(identity.copy(clientSecret = Identity.generateSecret()))
          .map(_.map(id => Secret(id.clientSecret)))
      case None =>
        Future.successful(None)
    }

  def addClientScope(id: String, clientScopeId: String): Future[Option[Unit]] = {
    repository.addScope(id, clientScopeId)
  }

  def deleteClientScope(id: String, clientScopeId: String): Future[Option[Unit]] = {
    repository.removeScope(id, clientScopeId)
  }

  def fetchIdentity(id: String): Future[Option[Identity]] = {
    repository.find(id)
  }

}
