package ru.org.codingteam.cttalk

import java.util.Date

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import play.api.test.PlaySpecification
import ru.org.codingteam.cttalk.models.{Message, Token}
import ru.org.codingteam.cttalk.services.MessagesServiceImpl
import ru.org.codingteam.cttalk.services.messaging.MessageReceiver

/**
 * Created by hgn on 25.10.2015.
 */
class MessagesServiceSpec extends PlaySpecification with Mockito {
  sequential

  private class MockReceiver extends MessageReceiver {
    private val list = List.newBuilder[Message]

    override def receive(message: Message): Boolean = {
      list += message
      true
    }

    def get: Seq[Message] = list.result()
  }

  "MessageService.send" should {
    "-- succeed when sending to existing recipient" in { implicit ee: ExecutionEnv =>
      val service = new MessagesServiceImpl
      val token: Token = Token("existing", "username")
      service.register(token, new MockReceiver)
      service.send(token, Message("sender", "receiver", wasRead = false, new Date, "message")) map {
        result => result mustEqual true
      } await
    }

    "-- fail when sending to unknown recipient" in { implicit ee: ExecutionEnv =>
      val service = new MessagesServiceImpl
      val token: Token = Token("unknown", "username")
      service.send(token, Message("sender", "receiver", wasRead = false, new Date, "message")) must throwA[Exception].await
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

      service.register(token, new MockReceiver) must throwA[Exception].await
    }
  }
}
