package ru.org.codingteam.cttalk.client

import ru.org.codingteam.cttalk.client.model.{Chat, Handle}

import scala.scalajs.js

/**
 * Created by hgn on 21.11.2015.
 */
@js.native
trait ChatboxScope extends RosterScope {

  var chat: Chat = js.native

  var self: Handle = js.native
  var text: String = js.native
}
