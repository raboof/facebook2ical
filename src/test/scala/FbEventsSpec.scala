import scala.io.Source

import spray.json._

import org.scalatest._

import FbEvents._

class FbEventsSpec extends WordSpec with Matchers with FbJsonMarshalling {

  "The fb json reader" should {
    "successfully read the example json" in {
      val string = Source.fromInputStream(getClass.getResourceAsStream("/events.json")).mkString
      val response = string.parseJson.convertTo[Response]
      val events = response.data
      events.size should be(25)
      events(0).id should be("978483572260625")
      events(0).name should be("Originators en DJ Dab en DJ The Shake (Manchester)")

      response.paging shouldEqual Some(Paging(Some("https://graph.facebook.com/v2.7/430160187055128/events?access_token=EAACEdEose0cBALl3AmHNEVjITZAjInBAhNJD0OC2aZAnNdh3E41CaHFNipwQ8ijWuvIkna9RZBpAY3ixWXo5974PL9oJOQvYY47qpcm7IouEDsnB6Q8ZCCZB6vB0BO4hD2HqBZAoXlGsImaN68zJ1uTJEEGy28DaydSpsArIrGugZDZD&limit=25&after=NDc0ODA0NjI2MDUxMjA1")))
    }
  }
}
