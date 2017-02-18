import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import spray.json._
import scalaj.http._

object FbEvents {
  case class FbEvent(id: String, name: String, description: Option[String], startTime: ZonedDateTime, endTime: Option[ZonedDateTime]);
  case class Cursors(before: String, after: String);
  case class Paging(cursors: Cursors);
  case class Response(data: List[FbEvent], paging: Option[Paging]);
}

trait FbEvents extends FbJsonMarshalling {
  import FbEvents._

  def getEvents(token: String, pageId: String, cursor: Option[String] = None): List[FbEvent] = {
    val url : String = cursor match {
      case None => s"https://graph.facebook.com/v2.7/$pageId/events?access_token=$token";
      case Some(after) => s"https://graph.facebook.com/v2.7/$pageId/events?access_token=$token&after=$after";
    }
    Http(url).asString match {
      case HttpResponse(body, 200, _) =>
        val response : Response = body.parseJson.convertTo[Response];
        val currentResults : List[FbEvent] = response.data;
        val nextCursor: Option[String] = response.paging map { _.cursors.after }
        nextCursor match {
           case None => currentResults
           case Some(cursor) => currentResults ::: getEvents(token, pageId, Some(cursor))
        }
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
  implicit val eventFormat = jsonFormat5(FbEvent);
  implicit val cursorsFormat = jsonFormat2(Cursors);
  implicit val pagingFormat = jsonFormat1(FbEvents.Paging);
  implicit val responseFormat: JsonReader[Response] = jsonFormat2(Response); 
}
