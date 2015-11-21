package ru.org.codingteam.cttalk.client.controllers

import com.greencatsoft.angularjs.core.HttpService
import com.greencatsoft.angularjs.{AbstractController, injectable}
import org.scalajs.dom.console
import ru.org.codingteam.cttalk.client.ChatScope
import ru.org.codingteam.cttalk.client.model.Chat
import ru.org.codingteam.cttalk.client.services.MessageService
import upickle.default._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}

/**
 * Created by hgn on 21.11.2015.
 */
@JSExport
@injectable("RosterController")
class RosterController(scope: ChatScope, $http: HttpService, messages: MessageService) extends AbstractController[ChatScope](scope) {

  $http.get[js.Array[js.Any]]("/api/chats").onComplete {
    case Success(chats) => scope.chats = chats.map {
      JSON.stringify(_)
    } map read[Chat]
    case Failure(reason) => console.error("Error while getting chats.")
  }

  @JSExport
  def select(chat: Chat): Unit = {
    scope.selected = chat
    scope.selected.messages.foreach {
      _.wasRead = true
    }
  }
}
