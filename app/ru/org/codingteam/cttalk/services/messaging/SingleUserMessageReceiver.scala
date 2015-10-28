package ru.org.codingteam.cttalk.services.messaging

import play.api.libs.concurrent.Execution.Implicits._
import ru.org.codingteam.cttalk.models.{Message, Token}
import ru.org.codingteam.cttalk.services.MessagesRepository

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
 * Created by hgn on 27.10.2015.
 */
class SingleUserMessageReceiver(token: Token, messagesRepository: MessagesRepository) extends MessageReceiver {

  override def receive(message: Message): Future[Boolean] = {
    Future.successful(true)
  }

  override def get(): Promise[Seq[Message]] = {
    val messagesPromise = Promise[Seq[Message]]()
    messagesRepository.getUnreadFor(token) onComplete {
      case Success(Seq()) => messagesPromise.failure(new RuntimeException) //todo wait for receive call and attempt recovery here
      case Success(seq) => messagesPromise.success(seq)
      case Failure(throwable) => messagesPromise.failure(throwable)
    }
    messagesPromise
  }
}
