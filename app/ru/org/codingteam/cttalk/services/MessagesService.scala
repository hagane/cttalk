package ru.org.codingteam.cttalk.services

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.libs.concurrent.Execution.Implicits._
import ru.org.codingteam.cttalk.models.{Message, Token}
import ru.org.codingteam.cttalk.services.messaging.{MessageReceiver, SingleUserMessageReceiver}

import scala.concurrent.Future

/**
 * Created by hgn on 25.10.2015.
 */
@ImplementedBy(classOf[MessagesServiceImpl])
trait MessagesService {
  def send(message: Message): Future[Boolean]

  def register(token: Token, receiver: MessageReceiver): Future[Token]
}

class MessagesServiceImpl @Inject()(messages: MessagesRepository, tokens: TokensRepository) extends MessagesService {
  val receivers = new ConcurrentHashMap[Token, MessageReceiver]

  override def send(message: Message): Future[Boolean] = {
    messages.save(message) flatMap { message =>
      tokens.getByHandle(message.receiver) map { tokens =>
        tokens map { token =>
          Option(receivers.replace(token, new SingleUserMessageReceiver(token, messages))) exists { receiver =>
            receiver.receive(message)
          }
        } reduce {
          _ || _
        }
      }
    }
  }

  override def register(token: Token, receiver: MessageReceiver): Future[Token] = {
    Option(receivers.putIfAbsent(token, receiver)) match {
      case None => Future.successful(token)
      case Some(_) => Future.failed(new RuntimeException("token already present"))
    }
  }
}
