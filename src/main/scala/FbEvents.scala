import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import spray.json._
import scalaj.http._

object FbEvents {
  case class FbEvent(id: String, name: String, description: Option[String], startTime: ZonedDateTime, endTime: Option[ZonedDateTime])
  case class Paging(next: Option[String])
  case class Response(data: List[FbEvent], paging: Option[Paging])
}

trait FbEvents extends FbJsonMarshalling {
  import FbEvents._

  def getEvents(url: String): List[FbEvent] = {
    Http(url).asString match {
      case HttpResponse(body, 200, _) =>
        val response : Response = body.parseJson.convertTo[Response];

        response.data ++ (response.paging.flatMap(_.next) match {
           case None => Nil
           case Some(newUrl) => getEvents(newUrl)
        })
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
  implicit val pagingFormat = jsonFormat1(FbEvents.Paging)
  implicit val responseFormat: JsonReader[Response] = jsonFormat2(Response)
}
