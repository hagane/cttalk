package ru.org.codingteam.cttalk.client

import com.greencatsoft.angularjs.core.Scope
import ru.org.codingteam.cttalk.client.model.Chat

import scala.scalajs.js

/**
 * Created by hgn on 21.11.2015.
 */
@js.native
trait ChatboxScope extends Scope {

  var chat: Chat = js.native

  var sender: String = js.native
  var text: String = js.native
}