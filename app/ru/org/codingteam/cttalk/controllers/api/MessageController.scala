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
class MessageController @Inject()(messagesService: MessagesService, tokensRepository: TokensRepository) extends Controller with JsonRequest with Secure {
  implicit val sendReads = ((JsPath \ "to").read[Handle] and
    (JsPath \ "text").read[String]
    ) tupled

  def send = withAuthCookie("token")(cookie => jsonAsync[(Handle, String)] {
    case (receiver, text) => tokensRepository.get(cookie.value) flatMap {
      case Some(token) => messagesService.send(Message(token.handle, receiver, wasRead = false, new Date, text))
      case None => Future.failed(new RuntimeException)
    } map {_ => Ok}
  })

  def receive = withAuthCookie("token") { cookie => {
    case _ => Future.successful(Ok)
  }
  }
}
