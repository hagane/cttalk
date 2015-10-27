package ru.org.codingteam.cttalk.services.messaging

import ru.org.codingteam.cttalk.models.Message
import ru.org.codingteam.cttalk.services.MessagesRepository

import scala.concurrent.Future

/**
 * Created by hgn on 27.10.2015.
 */
class SingleUserMessageReceiver(messagesRepository: MessagesRepository) extends MessageReceiver {
  override def receive(message: Message): Future[Boolean] = {
    messagesRepository.save(message) map { _ => true } recover { case _ => false }
  }
}
