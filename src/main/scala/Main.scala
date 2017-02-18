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
    val description: String = event.description.getOrElse("");
    Event(
      uid = Uid(event.id),
      dtstart = event.startTime,
      summary = Summary(event.name),
      description = Description(description),
      url = Url(s"https://www.facebook.com/events/${event.id}/")
    )
  }

  def getICalendar(token: String, pageId: String): String = {
    asIcal(Calendar(
      prodid = Prodid("-//raboof/facebook2ical//NONSGML v1.0//NL"),
      events = getEvents(token, pageId).map(convert(_))))
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
