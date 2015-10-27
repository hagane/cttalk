package ru.org.codingteam.cttalk.services.messaging

import ru.org.codingteam.cttalk.models.Message

import scala.concurrent.{Future, Promise}

/**
 * Created by hgn on 25.10.2015.
 */
trait MessageReceiver {
  def receive(message: Message): Future[Boolean]

  def get(): Promise[Seq[Message]]
}
