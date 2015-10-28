package ru.org.codingteam.cttalk

import play.api.test.PlaySpecification

/**
 * Created by hgn on 28.10.2015.
 */
class SingleUserMessageReceiverSpec extends PlaySpecification {
  "SingleUserMessageReceiver.receive" should {

    "-- trigger fulfillment of a promise from .get" in {
      todo
    }
  }

  "SingleUserMessageReceiver.get" should {
    "-- return an eventually successfull promise if there are unread messages" in {
      todo
    }

    "-- return a promise that will be successfull after receiving a message" in {
      todo
    }

    "-- return a promise that will be failed if call to repository was failed" in {
      todo
    }
  }
}
