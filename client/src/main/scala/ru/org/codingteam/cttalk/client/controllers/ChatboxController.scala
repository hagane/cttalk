package ru.org.codingteam.cttalk.client.controllers

import com.greencatsoft.angularjs.{AbstractController, injectable}
import org.scalajs.dom.{console, window}
import ru.org.codingteam.cttalk.client.ChatboxScope
import ru.org.codingteam.cttalk.client.model.{ReceivedMessage, SentMessage}
import ru.org.codingteam.cttalk.client.services.{AuthenticationService, ChatService, MessageService}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}

/**
 * Created by hgn on 21.11.2015.
 */
@JSExport
@injectable("ChatboxController")
class ChatboxController(scope: ChatboxScope, chats: ChatService, messages: MessageService, authenticationService: AuthenticationService)
  extends AbstractController[ChatboxScope](scope) {

  authenticationService.self().onComplete {
    case Success(handle) => scope.self = handle
    case Failure(error) =>
      console.log(s"Error while fetching self: $error")
      window.location.href = "/auth"
  }

  chats.onSelect {
    scope.chat = _
  }

  @JSExport
  def post = {
    val message = ReceivedMessage(scope.self, scope.chat.handle, wasRead = false, js.Date().toString, scope.text)
    scope.chat.messages.push(message)
    messages.send(SentMessage(scope.chat.handle, scope.text))
  }
}
