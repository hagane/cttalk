package ru.org.codingteam.cttalk.client.controllers

import com.greencatsoft.angularjs.{AbstractController, injectable}
import ru.org.codingteam.cttalk.client.ChatboxScope
import ru.org.codingteam.cttalk.client.model.{Handle, ReceivedMessage}
import ru.org.codingteam.cttalk.client.services.{ChatService, MessageService}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

/**
 * Created by hgn on 21.11.2015.
 */
@JSExport
@injectable("ChatboxController")
class ChatboxController(scope: ChatboxScope, chats: ChatService, messages: MessageService)
  extends AbstractController[ChatboxScope](scope) {
  require(messages != null, s"No MessageServiceSupplied")

  chats.onSelect {
    scope.chat = _
  }

  @JSExport
  def post = {
    val message = ReceivedMessage(Handle(scope.sender), scope.chat.handle, wasRead = false, js.Date().toString, scope.text)
    scope.chat.messages.push(message)
  }
}
