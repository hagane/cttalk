package ru.org.codingteam.cttalk.services

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.libs.concurrent.Execution.Implicits._
import ru.org.codingteam.cttalk.models.{Handle, Message, Token}
import ru.org.codingteam.cttalk.services.messaging.MessageReceiver

import scala.concurrent.Future

/**
 * Created by hgn on 25.10.2015.
 */
@ImplementedBy(classOf[MessagesServiceImpl])
trait MessagesService {
  def send(to: Handle, message: Message): Future[Boolean]

  def register(token: Token, receiver: MessageReceiver): Future[Token]
}

class MessagesServiceImpl @Inject()(messages: MessagesRepository) extends MessagesService {
  val receivers = new ConcurrentHashMap[Token, MessageReceiver]

  override def send(to: Handle, message: Message): Future[Boolean] = {
    Option(receivers.get(to)) map { receiver =>
      messages.save(message) flatMap receiver.receive
    } getOrElse Future.failed(new RuntimeException("token not registered"))
  }

  override def register(token: Token, receiver: MessageReceiver): Future[Token] = {
    Option(receivers.putIfAbsent(token, receiver)) match {
      case None => Future.successful(token)
      case Some(_) => Future.failed(new RuntimeException("token already present"))
    }
  }
}
