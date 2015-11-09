package ru.org.codingteam.cttalk.controllers.api

import play.api.libs.json._
import play.api.mvc._

import scala.util.Random

/**
 * Created by hgn on 26.08.2015.
 */
class ChatController extends Controller {

  def chats = Action {
    val chats = Json.arr(
      Json.obj(
        "name" -> "Codingteam",
        "token" -> "ct",
        "messages" -> Json.arr()
      )
    )
    Ok(Json.stringify(chats))
  }

  def unread(token: String) = Action {
    Ok(Json.stringify(JsNumber(new Random().nextInt(20))))
  }

}
