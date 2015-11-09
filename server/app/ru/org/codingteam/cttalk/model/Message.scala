package ru.org.codingteam.cttalk.model

import java.util.Date

/**
 * Created by hgn on 25.10.2015.
 */
case class Message(sender: Handle, receiver: Handle, wasRead: Boolean, moment: Date, text: String) {
  def id = sender.value + receiver.value + moment.getTime.toHexString
}
