import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import spray.json._
import scalaj.http._

object FbEvents {
  case class FbEvent(id: String, name: String, description: String, startTime: ZonedDateTime, endTime: Option[ZonedDateTime]);
  case class Response(data: List[FbEvent]);
}

trait FbEvents extends FbJsonMarshalling {
  import FbEvents._

  def getEvents(token: String, pageId: String): List[FbEvent] = {
    val url = s"https://graph.facebook.com/v2.7/$pageId/events?access_token=$token";
    Http(url).asString match {
      case HttpResponse(body, 200, _) => 
        body.parseJson.convertTo[Response].data
    };
  }
}

trait FbJsonMarshalling extends SnakifiedSprayJsonSupport {
  import FbEvents._
  import DefaultJsonProtocol._

  implicit val dateTimeFormat = lift(new JsonReader[ZonedDateTime] {
    override def read(json: JsValue) = json match {
      case JsString(value) => ZonedDateTime.parse(value.replaceAll("00$", ":00"), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
      case _ => deserializationError("Not a string: $json")
    }
  })
  implicit val eventFormat = jsonFormat5(FbEvent)
  implicit val responseFormat: JsonReader[Response] = jsonFormat1(Response)
}
