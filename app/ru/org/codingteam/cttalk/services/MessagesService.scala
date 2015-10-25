package ru.org.codingteam.cttalk.services

import java.util
import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

import com.google.inject.ImplementedBy
import ru.org.codingteam.cttalk.models.{MessageReceiver, Message, User}
import ru.org.codingteam.cttalk.util.JavaFunctionConversions._

import scala.concurrent.Future

/**
 * Created by hgn on 25.10.2015.
 */
@ImplementedBy(classOf[MessagesServiceImpl])
trait MessagesService {
  def send(to: String, message: Message): Future[Boolean]

  def get(receiverToken: String): Future[Seq[Message]]

  def register(token: String, receiver: MessageReceiver)
}

class MessagesServiceImpl extends MessagesService {
  val receivers = new ConcurrentHashMap[String, util.Queue[Message]]

  override def send(to: String, message: Message): Future[Boolean] = {
    val queue: util.Queue[Message] = receivers.computeIfAbsent(to, asJavaFunction {_ => new ConcurrentLinkedQueue[Message]()})
    Future.successful(queue.offer(message))
  }

  override def get(receiverToken: String): Future[Seq[Message]] = {
    val messages: Option[util.Queue[Message]] = Option(receivers.get(receiverToken))
    Future.successful(messages map consuming getOrElse Seq())
  }

  override def register(token: String, receiver: MessageReceiver): Unit = {}

  private def consuming[T](queue: util.Queue[T]):Seq[T] = {
    Option(queue.poll) match {
      case None => Seq()
      case Some(t) => Seq(t) ++ consuming(queue)
    }
  }
}
