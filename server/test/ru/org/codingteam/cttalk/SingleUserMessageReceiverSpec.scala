package ru.org.codingteam.cttalk

import org.specs2.mock.Mockito
import play.api.test.PlaySpecification
import ru.org.codingteam.cttalk.models.{Message, Token}
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
}
