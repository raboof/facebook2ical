import scala.language.implicitConversions

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import icalendar._
import icalendar.Properties._
import icalendar.CalendarProperties._
import icalendar.ical.Writer._

trait Main extends FbEvents {
  implicit def liftOption[T](value: T): Option[T] = Some(value)

  def convert(event: FbEvents.FbEvent): Event = {
    Event(
      uid = Uid(event.id),
      dtstart = event.startTime,
      summary = Summary(event.name),
      description = event.description.map(Description(_)),
      url = Url(s"https://www.facebook.com/events/${event.id}/")
    )
  }

  def getICalendar(token: String, pageId: String): String = {
    asIcal(Calendar(
      prodid = Prodid("-//raboof/facebook2ical//NONSGML v1.0//NL"),
      events = getEvents(token, pageId).map(convert(_))))
  }
}

class MainLambda extends Main with SnakifiedSprayJsonSupport {
  import java.io.{ InputStream, OutputStream }
  import scala.io.Source
  import spray.json._

  case class Params(token: String, pageId: String)
  implicit val paramsReader = jsonFormat2(Params)

  case class Request(params: Params)
  implicit val requestReader = jsonFormat1(Request)

  def handleRequest(inputStream: InputStream, outputStream: OutputStream): Unit = {
      val params = Source.fromInputStream(inputStream).mkString.parseJson.convertTo[Request].params
      outputStream.write(getICalendar(params.token, params.pageId).getBytes("UTF-8"))
  }
}

object MainApp extends App with Main {
  args.toList match {
    case token :: pageId :: Nil =>
      print(getICalendar(token, pageId));
    case _ =>
      println("Usage: fb2ical <token> <page-id>");
  }
}
