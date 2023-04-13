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

package uk.gov.hmrc.identitymanagementservicestubs.services

import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.identitymanagementservicestubs.models.{ClientResponse, Identity}
import uk.gov.hmrc.identitymanagementservicestubs.repositories.IdentityRepository

import scala.concurrent.Future

class IdentityServiceSpec extends AsyncFreeSpec with Matchers with MockitoSugar with ScalaFutures  {
  "createClient" - {
       "must build the correct Identity and submit it to the repository" in {

           val repository = mock[IdentityRepository]
           val service = new IdentityService(repository)
           val identity = Identity("test-app-name", "This is a test application", Some("63bebf8bbbeccc26c12294e5"), "client-secret-bla-bla")
           val expected = ClientResponse("63bebf8bbbeccc26c12294e5", "client-secret-bla-bla")
           when(repository.insert(any[Identity])).thenReturn(Future.successful(identity))
           service.createIdentity(identity.copy(clientId = None)).map {
             actual =>
               actual mustBe Some(expected)
       }
  }
}}
