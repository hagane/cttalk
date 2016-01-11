package ru.org.codingteam.cttalk.client.controllers

import com.greencatsoft.angularjs.core.Timeout
import com.greencatsoft.angularjs.{AbstractController, injectable}
import org.scalajs.dom.{console, window}
import ru.org.codingteam.cttalk.client.model.{Chat, ReceivedMessage, SentMessage}
import ru.org.codingteam.cttalk.client.services.{AuthenticationService, ChatService, MessageService}
import ru.org.codingteam.cttalk.client.{ChatboxScope, RosterScope}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}

/**
 * Created by hgn on 21.11.2015.
 */
@JSExport
@injectable("ChatboxController")
class ChatboxController(scope: ChatboxScope, rosterScope: RosterScope, chats: ChatService, messages: MessageService, authenticationService: AuthenticationService, timeout: Timeout)
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

  receive()

  @JSExport
  def post() = {
    val message = ReceivedMessage(scope.self, scope.chat.handle, wasRead = false, scope.text)
    scope.chat.messages.push(message)
    messages.send(SentMessage(scope.chat.handle, scope.text))
  }

  def receive(): Unit = {
    def r = {
      //todo this should be simplified somehow
      messages.receive().andThen {
        case Success(receivedMessages) => {
          receivedMessages.foreach { message =>
            val from = message.sender
            rosterScope.chats.find(_.handle == from).orElse {
              val newChat = Chat(from, js.Array())
              rosterScope.chats.push(newChat)
              chats.addChat(newChat).onFailure { case error =>
                console.log(s"Cannot add [$newChat] to roster: $error")
                rosterScope.chats -= newChat
              }
              Some(newChat)
            } foreach { chat =>
              chat.messages.push(message)
              chat.unread += 1
            }
          }
        }
        case Failure(error) => console.log(s"Error while receiving: $error")
      }.andThen { case _ => receive() }
    }
    timeout({
      r _
    }, 10000) //TODO make this configurable or something
  }
}
