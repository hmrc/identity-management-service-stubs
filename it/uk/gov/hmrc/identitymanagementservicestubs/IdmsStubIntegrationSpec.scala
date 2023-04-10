package uk.gov.hmrc.identitymanagementservicestubs


import org.scalacheck.Arbitrary
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.libs.json.Json
import uk.gov.hmrc.identitymanagementservicestubs.models.{Client, ClientResponse, Identity}
import uk.gov.hmrc.identitymanagementservicestubs.repositories.IdentityRepository
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import play.api.{Application => GuideApplication}
import org.scalacheck.{Arbitrary, Gen}

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.inject.bind
class IdmsStubIntegrationSpec extends AnyWordSpec
  with Matchers
  with OptionValues
  with GuiceOneServerPerSuite
  with DefaultPlayMongoRepositorySupport[Identity]
  with ScalaCheckPropertyChecks {

  private val wsClient = app.injector.instanceOf[WSClient]
  private val baseUrl = s"http://localhost:$port"



  override def fakeApplication(): GuideApplication =
    GuiceApplicationBuilder()
      .overrides(
        bind[IdentityRepository].toInstance(repository)
      )
      .configure("metrics.enabled" -> false)
      .build()

  override protected val repository: IdentityRepository = new IdentityRepository(mongoComponent)

  implicit val clientGenerator: Arbitrary[Client] =
    Arbitrary {
      for {
        applicationName <- Gen.alphaStr
        description <- Gen.alphaStr
      } yield Client(applicationName,description)
    }

  "POST to register a new Identity" should {
    "respond with a 201 Created and body containing the clientResponse" in {
      forAll { client: Client =>
        val response =
          wsClient
            .url(s"$baseUrl/identity-management-service-stubs/clients")
            .addHttpHeaders(("Content", "application/json"))
            .post(Json.toJson(client))
            .futureValue

        response.status shouldBe 201
        noException should be thrownBy response.json.as[ClientResponse]
      }
    }
  }

}
