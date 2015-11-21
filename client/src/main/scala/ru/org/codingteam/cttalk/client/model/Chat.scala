package ru.org.codingteam.cttalk.client.model

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

/**
 * Created by hgn on 21.11.2015.
 */
@JSExportAll
case class Chat(handle: Handle, name: String, var messages: js.Array[ReceivedMessage]) {

  def unread = messages.filter {
    !_.wasRead
  }.length

}
