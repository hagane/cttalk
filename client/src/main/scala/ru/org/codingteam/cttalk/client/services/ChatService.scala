package ru.org.codingteam.cttalk.client.services

import com.greencatsoft.angularjs.core.HttpService
import com.greencatsoft.angularjs.{Factory, Service, injectable}
import ru.org.codingteam.cttalk.client.model.Chat
import upickle.default._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport

/**
 * Created by hgn on 21.11.2015.
 */
@JSExport
@injectable("ChatService")
class ChatService(http: HttpService) extends Service {
  require(http != null, s"No http service supplied.")

  var listener: Chat => Unit = { _ => () }

  def getChats = {
    http.get[js.Array[js.Any]]("/api/chats") map {
      array => array map {
        JSON.stringify(_)
      } map read[Chat]
    }
  }

  def select(chat: Chat): Unit = {
    chat.messages.foreach {
      _.wasRead = true
    }
    listener.apply(chat)
  }

  def onSelect(listener: Chat => Unit) = {
    this.listener = listener
  }
}

@JSExport
@injectable("ChatService")
class ChatServiceFactory(http: HttpService) extends Factory[ChatService] {
  override def apply(): ChatService = new ChatService(http)
}
