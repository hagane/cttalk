package ru.org.codingteam.cttalk.client.controllers

import com.greencatsoft.angularjs.core.HttpService
import com.greencatsoft.angularjs.{AbstractController, injectable}
import org.scalajs.dom.console
import ru.org.codingteam.cttalk.client.ChatScope
import ru.org.codingteam.cttalk.client.model.Chat

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}

/**
 * Created by hgn on 21.11.2015.
 */
@JSExport
@injectable("RosterController")
class RosterController(scope: ChatScope, $http: HttpService) extends AbstractController[ChatScope](scope) {

  $http.get[js.Array[Chat]]("/api/chats").onComplete {
    case Success(chats) => scope.chats = chats
    case Failure(reason) => console.log("Error while getting chats.")
  }

  @JSExport
  def select(chat: Chat): Unit = {
    scope.selected = chat
    chat.messages.foreach {
      _.wasRead = true
    }
  }

}
