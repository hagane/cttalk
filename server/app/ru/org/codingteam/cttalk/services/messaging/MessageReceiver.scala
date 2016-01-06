package ru.org.codingteam.cttalk.services.messaging

import ru.org.codingteam.cttalk.model.{Message, Token}

import scala.concurrent.Promise

/**
 * Created by hgn on 25.10.2015.
 */
trait MessageReceiver {
  def receive(message: Message): Boolean

  def get(): Promise[Seq[Message]]

  def token(): Token
}
