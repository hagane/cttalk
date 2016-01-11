package ru.org.codingteam.cttalk.client.model

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

/**
 * Created by hgn on 21.11.2015.
 */
@JSExportAll
case class Chat(var handle: Handle, var messages: js.Array[ReceivedMessage]) {

  var unread = messages.filter {
    !_.wasRead
  }.length

}
