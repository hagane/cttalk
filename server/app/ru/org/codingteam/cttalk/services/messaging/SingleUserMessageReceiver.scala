package ru.org.codingteam.cttalk.services.messaging

import ru.org.codingteam.cttalk.model.{Message, Token}

import scala.concurrent.Promise

/**
 * Created by hgn on 27.10.2015.
 */
class SingleUserMessageReceiver(receiverToken: Token) extends MessageReceiver {
  val promise = Promise[Seq[Message]]()

  override def receive(message: Message): Boolean = {
    promise.trySuccess(Seq(message))
  }

  override def get(): Promise[Seq[Message]] = {
    promise
  }

  override def token(): Token = receiverToken
}
