package ru.org.codingteam.cttalk.services

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.libs.concurrent.Execution.Implicits._
import ru.org.codingteam.cttalk.model.{Message, Token}
import ru.org.codingteam.cttalk.services.messaging.{MessageReceiver, SingleUserMessageReceiver}

import scala.concurrent.Future

/**
 * Created by hgn on 25.10.2015.
 */
@ImplementedBy(classOf[MessagesServiceImpl])
trait MessagesService {
  def send(message: Message): Future[Boolean]

  def get(token: Token): Future[Seq[Message]]

  def register(token: Token, receiver: MessageReceiver): Future[Token]
}

class MessagesServiceImpl @Inject()(messages: MessagesRepository, tokens: TokensRepository) extends MessagesService {
  val receivers = new ConcurrentHashMap[Token, MessageReceiver]

  override def send(message: Message): Future[Boolean] = {
    messages.save(message) flatMap { message =>
      tokens.getByHandle(message.receiver) map { tokens =>
        tokens map { token =>
          Option(receivers.replace(token, new SingleUserMessageReceiver(token))) exists { receiver =>
            receiver.receive(message)
          }
        } reduce (_ || _)
      }
    }
  }

  override def register(token: Token, receiver: MessageReceiver): Future[Token] = {
    Option(receivers.putIfAbsent(token, receiver)) match {
      case None => Future.successful(token)
      case Some(existingReceiver) => Future.successful(existingReceiver.token())
    }
  }

  override def get(token: Token): Future[Seq[Message]] = {
    Option(receivers.get(token)) map {
      _.get().future
    } getOrElse {
      Future.failed(new RuntimeException)
    }
  }
}
