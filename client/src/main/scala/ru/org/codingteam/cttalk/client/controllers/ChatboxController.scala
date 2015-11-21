package ru.org.codingteam.cttalk.client.controllers

import com.greencatsoft.angularjs.{AbstractController, injectable}
import ru.org.codingteam.cttalk.client.ChatScope
import ru.org.codingteam.cttalk.client.model.{Handle, ReceivedMessage}
import ru.org.codingteam.cttalk.client.services.MessageService

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

/**
 * Created by hgn on 21.11.2015.
 */
@JSExport
@injectable("ChatboxController")
class ChatboxController(scope: ChatScope, messages: MessageService) extends AbstractController[ChatScope](scope) {
  require(messages != null, s"No MessageServiceSupplied")

  @JSExport
  def post = {
    val message = ReceivedMessage(Handle(scope.sender), scope.selected.handle, wasRead = false, js.Date().toString, scope.text)
    scope.selected.messages.push(message)
  }
}
