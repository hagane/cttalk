package ru.org.codingteam.cttalk

import org.specs2.mock.Mockito
import play.api.test.PlaySpecification
import ru.org.codingteam.cttalk.model.{Message, Token}
import ru.org.codingteam.cttalk.services.messaging.SingleUserMessageReceiver

/**
 * Created by hgn on 28.10.2015.
 */
class SingleUserMessageReceiverSpec extends PlaySpecification with Mockito {
  "SingleUserMessageReceiver.receive" should {

    "-- trigger fulfillment of a promise from .get" in {
      val receiver = new SingleUserMessageReceiver(mock[Token])
      val promise = receiver.get()

      promise.isCompleted mustEqual false

      receiver.receive(mock[Message])

      promise.isCompleted mustEqual true
    }
  }

  "SingleUserMessageReceiver.token" should {
    "-- return token this receiver is registered with" in {
      val token = mock[Token]
      val receiver = new SingleUserMessageReceiver(token)
      receiver.token() mustEqual token
    }
  }
}
