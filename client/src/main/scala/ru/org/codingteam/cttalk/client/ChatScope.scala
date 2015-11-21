package ru.org.codingteam.cttalk.client

import com.greencatsoft.angularjs.core.Scope
import ru.org.codingteam.cttalk.client.model.Chat

import scala.scalajs.js

/**
 * Created by hgn on 21.11.2015.
 */
@js.native
trait ChatScope extends Scope {
  var chats: js.Array[Chat] = js.native
  var selected: Chat = js.native
}
