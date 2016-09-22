import scala.io.Source

import spray.json._

import org.scalatest._

class MainSpec extends WordSpec with Matchers with Main {

  "The main code" should {
    "successfully convert an event" in {
      val string = Source.fromInputStream(getClass.getResourceAsStream("/events.json")).mkString
      val events = string.parseJson.convertTo[FbEvents.Response].data.map(convert(_))
      events.size should be(25)
    }
  }
}
