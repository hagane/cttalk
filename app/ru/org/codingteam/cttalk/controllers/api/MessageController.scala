package ru.org.codingteam.cttalk.controllers.api

import java.util.Date
import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import ru.org.codingteam.cttalk.models.Handle._
import ru.org.codingteam.cttalk.models.{Handle, Message}
import ru.org.codingteam.cttalk.services.{MessagesService, TokensRepository}

import scala.concurrent.Future

/**
 * Created by hgn on 29.10.2015.
 */
class MessageController @Inject()(messages: MessagesService, tokens: TokensRepository) extends Controller with JsonRequest with Secure {
  implicit val sendReads = ((JsPath \ "to").read[Handle] and
    (JsPath \ "text").read[String]
    ) tupled

  implicit val receiveWrites = ((JsPath \ "_id").write[String] and
      (JsPath \ "sender").write[Handle] and
      (JsPath \ "receiver").write[Handle] and
      (JsPath \ "wasRead").write[Boolean] and
      (JsPath \ "moment").write[Date] and
      (JsPath \ "text").write[String]) {m: Message => (m.id, m.sender, m.receiver, m.wasRead, m.moment, m.text)}

  def send = withAuthCookie("token")(cookie => jsonAsync[(Handle, String)] {
    case (receiver, text) => tokens.get(cookie.value) flatMap {
      case Some(token) => messages.send(Message(token.handle, receiver, wasRead = false, new Date, text)) map {
        case false => NotFound
        case true => Ok
      }
      case None => Future.failed(new RuntimeException)
    } recover {
      case _ => Unauthorized
    }
  })

  def receive = withAuthCookie("token") { cookie => { implicit request =>
    tokens.get(cookie.value) flatMap {
      case Some(token) => messages.get(token) map { seqOfMessages => Ok(Json.toJson(seqOfMessages)) }
      case None => Future.successful(Unauthorized)
    }
  }
  }
}
