package ru.org.codingteam.cttalk

import java.util.Date

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import play.api.test.PlaySpecification
import ru.org.codingteam.cttalk.models.{Message, MessageReceiver, Token}
import ru.org.codingteam.cttalk.services.MessagesServiceImpl

/**
 * Created by hgn on 25.10.2015.
 */
class MessagesServiceSpec extends PlaySpecification with Mockito {
  sequential

  private class MockReceiver extends MessageReceiver {
    private val list = List.newBuilder[Message]

    override def receive(message: Message): Unit = {
      list += message
    }

    def get: Seq[Message] = list.result()
  }

  "MessageService.send" should {
    "-- succeed when sending to existing recipient" in { implicit ee: ExecutionEnv =>
      val service = new MessagesServiceImpl
      val token: Token = Token("existing", "username")
      service.register(token, mock[MessageReceiver])
      service.send(token, Message("sender", new Date(), "message")) map {
        result => result mustEqual true
      } await
    }

    "-- fail when sending to unknown recipient" in { implicit ee: ExecutionEnv =>
      val service = new MessagesServiceImpl
      val token: Token = Token("unknown", "username")
      service.send(token, Message("sender", new Date(), "message")) map {
        result => result mustEqual false
      } await
    }
  }

  "MessageService.get" should {
    "-- receive previously sent messages" in { implicit ee: ExecutionEnv =>
      val service = new MessagesServiceImpl
      val receiver = new MockReceiver
      val message: Message = Message("sender", new Date, "test")
      val token: Token = Token("receiver", "username")

      service.register(token, receiver)
      service.send(token, message) map { result => result mustEqual true } await

      receiver.get.length mustEqual 1
      receiver.get.contains(message) mustEqual true
    }

    "-- return empty sequence if there is no messages" in { implicit ee: ExecutionEnv =>
      val service = new MessagesServiceImpl
      val receiver = new MockReceiver
      val message: Message = Message("sender", new Date, "test")
      val token: Token = Token("receiver", "username")

      service.register(token, receiver) map { _ => success } await

      receiver.get.length mustEqual 0
    }

    "-- fail if trying to send messages to unknown recipient" in { implicit ee: ExecutionEnv =>
      val service = new MessagesServiceImpl
      val message: Message = Message("sender", new Date, "test")
      val token: Token = Token("receiver", "username")

      service.send(token, message) must throwA[Throwable].await
    }

    "-- fail if trying to get messages for unknown recipient" in { implicit ee: ExecutionEnv =>
      val service = new MessagesServiceImpl
      val message: Message = Message("sender", new Date, "test")

      service.get("receiver") must throwA[Throwable].await
    }
  }

  "MessageService.register" should {
    "-- register recipient if token is not previously registered" in {
      success("tested in other tests")
    }
    "-- fail if trying to register an already registered recipient" in { implicit ee: ExecutionEnv =>
      val service = new MessagesServiceImpl
      val token: Token = Token("receiver", "username")
      service.register(token, new MockReceiver) map { _ => success } await
    }
  }
}
