package ru.org.codingteam.cttalk.models

import java.util.Date

/**
 * Created by hgn on 25.10.2015.
 */
case class Message(senderToken: String, receiverToken: String, wasRead: Boolean, moment: Date, text: String) {
  def id = senderToken + receiverToken + moment.getTime.toHexString
}
