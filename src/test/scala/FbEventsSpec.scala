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

      response.paging shouldEqual Some(Paging(Cursors("OTc4NDgzNTcyMjYwNjI1", "NDc0ODA0NjI2MDUxMjA1")))
    }
  }
}
