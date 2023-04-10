package uk.gov.hmrc.identitymanagementservicestubs.repositories


import org.mockito.ArgumentMatchers.any
import org.mockito.captor.ArgCaptor
import org.mockito.{ArgumentMatchers, MockitoSugar}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json._
import uk.gov.hmrc.identitymanagementservicestubs.models.Identity
import uk.gov.hmrc.identitymanagementservicestubs.repositories.IdentityRepository.mongoIdentityFormat
class IdentityRepositorySpec extends AsyncFreeSpec with Matchers with MockitoSugar with ScalaFutures {
  "JSON serialisation and deserialisation" - {
    "must successfully deserialise JSON to create an Identity object" in {
      val json = Json.parse(
        s"""
           |{
           |"_id":{"$$oid":"63bebf8bbbeccc26c12294e5"},
           |"applicationName":"test-app-name",
           |"description" : "This is a test application",
           |"clientSecret" : "client-secret-bla-bla"
           |}
           """.stripMargin)
      val result = json.validate(mongoIdentityFormat)
      result mustBe a[JsSuccess[_]]
      val expected = Identity("test-app-name", "This is a test application", Some("63bebf8bbbeccc26c12294e5"), "client-secret-bla-bla")
      result.get mustBe expected
    }

  }
}
