package ru.org.codingteam.cttalk.client.model

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

/**
 * Created by hgn on 10.11.2015.
 */
@JSExportAll
case class ReceivedMessage(sender: Handle, receiver: Handle, var wasRead: Boolean, text: String) {
  val date = js.Date()
}
