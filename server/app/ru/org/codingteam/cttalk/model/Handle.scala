package ru.org.codingteam.cttalk.model

import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
 * Created by hgn on 28.10.2015.
 */
sealed trait Handle {
  def value: String
}

case class UserHandle(username: String) extends Handle {
  override def value: String = "user:" + username
}

case class ChatHandle(chatId: String) extends Handle {
  override def value: String = "chat:" + chatId
}

object Handle {
  implicit val userHandleReads: Reads[Handle] = (JsPath \ "user").read[String].map(UserHandle) or
    (JsPath \ "chat").read[String].map(ChatHandle)

  implicit val userHandleWrites = new Writes[Handle] {
    override def writes(handle: Handle): JsValue = handle match {
      case UserHandle(username) => Json.obj("user" -> username)
      case ChatHandle(chatId) => Json.obj("chat" -> chatId)
    }
  }
}
