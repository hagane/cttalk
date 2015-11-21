package ru.org.codingteam.cttalk.client.controllers

import com.greencatsoft.angularjs.{AbstractController, injectable}
import org.scalajs.dom.console
import ru.org.codingteam.cttalk.client.RosterScope
import ru.org.codingteam.cttalk.client.model.Chat
import ru.org.codingteam.cttalk.client.services.{ChatService, MessageService}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}

/**
 * Created by hgn on 21.11.2015.
 */
@JSExport
@injectable("RosterController")
class RosterController(scope: RosterScope, chats: ChatService, messages: MessageService) extends AbstractController[RosterScope](scope) {

  chats.getChats.onComplete {
    case Success(array) => scope.chats = array
    case Failure(error) => handleError(error)
  }

  @JSExport
  def select(chat: Chat): Unit = {
    scope.selected = chat
    chats.select(chat)
  }

  def handleError(error: Throwable): Unit = {
    console.error(s"Error while getting chats: $error")
  }
}
