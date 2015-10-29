package ru.org.codingteam.cttalk.controllers.api

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import ru.org.codingteam.cttalk.models.Handle

import scala.concurrent.Future

/**
 * Created by hgn on 29.10.2015.
 */
class MessageController extends Controller with JsonRequest with Secure {
  implicit val sendReads = ((JsPath \ "to").read[Handle] and
    (JsPath \ "text").read[String]
    ) tupled

  def send = withAuthCookie("token")(cookie => jsonAsync[(Handle, String)] {
    case _ => Future.successful(Ok)
  })

  def receive = withAuthCookie("token") { cookie => {
    case _ => Future.successful(Ok)
  }
  }
}
