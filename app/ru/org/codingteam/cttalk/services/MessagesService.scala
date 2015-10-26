package ru.org.codingteam.cttalk.services

import java.util
import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

import com.google.inject.ImplementedBy
import ru.org.codingteam.cttalk.models.{Message, MessageReceiver, Token}
import ru.org.codingteam.cttalk.util.JavaFunctionConversions._

import scala.concurrent.Future

/**
 * Created by hgn on 25.10.2015.
 */
@ImplementedBy(classOf[MessagesServiceImpl])
trait MessagesService {
  def send(to: Token, message: Message): Future[Boolean]

  def register(token: Token, receiver: MessageReceiver): Future[Token]
}

class MessagesServiceImpl extends MessagesService {
  val receivers = new ConcurrentHashMap[Token, MessageReceiver]

  override def send(to: Token, message: Message): Future[Boolean] = {
    Option(receivers.get(to)) map { receiver =>
      Future.successful(receiver.receive(message))
    } getOrElse Future.failed(new RuntimeException("token not registered"))
  }

  override def register(token: Token, receiver: MessageReceiver): Future[Token] = {
    Option(receivers.putIfAbsent(token, receiver)) match {
      case None => Future.successful(token)
      case Some(_) => Future.failed(new RuntimeException("token already present"))
    }
  }
}
