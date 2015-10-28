package ru.org.codingteam.cttalk.controllers.api

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import ru.org.codingteam.cttalk.models.Handle

import scala.concurrent.Future

/**
 * Created by hgn on 29.10.2015.
 */
class MessageController extends Controller with JsonRequest {
  implicit val sendReads = ((JsPath \ "token").read[String] and
    (JsPath \ "to").read[Handle] and
    (JsPath \ "text").read[String]
    ) tupled

  implicit val receiveReads = (JsPath \ "token").read[String]

  def send = jsonAsync[(String, Handle, String)] {
    case _ => Future.successful(Ok)
  }

  def receive = jsonAsync[String] {
    case _ => Future.successful(Ok)
  }
}
