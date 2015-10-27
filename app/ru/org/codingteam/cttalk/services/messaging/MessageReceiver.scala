package ru.org.codingteam.cttalk.services.messaging

import ru.org.codingteam.cttalk.models.Message

/**
 * Created by hgn on 25.10.2015.
 */
trait MessageReceiver {
  def receive(message: Message): Boolean
}
