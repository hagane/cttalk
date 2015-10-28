package ru.org.codingteam.cttalk.services.messaging

import ru.org.codingteam.cttalk.models.{Message, Token}

import scala.concurrent.Promise

/**
 * Created by hgn on 27.10.2015.
 */
class SingleUserMessageReceiver(token: Token) extends MessageReceiver {
  val promise = Promise[Seq[Message]]()

  override def receive(message: Message): Boolean = {
    promise.trySuccess(Seq(message))
  }

  override def get(): Promise[Seq[Message]] = {
    promise
  }
}
