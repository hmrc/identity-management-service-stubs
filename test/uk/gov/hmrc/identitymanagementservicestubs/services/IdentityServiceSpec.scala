package uk.gov.hmrc.identitymanagementservicestubs.services

import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentMatchers, MockitoSugar}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.identitymanagementservicestubs.models.{ClientResponse, Identity}
import uk.gov.hmrc.identitymanagementservicestubs.repositories.IdentityRepository

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{DurationInt, SECONDS}

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
