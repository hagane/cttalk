package ru.org.codingteam.cttalk

import play.api.test.PlaySpecification

/**
 * Created by hgn on 29.10.2015.
 */
class MessageControllerSpec extends PlaySpecification {
  "MessageController.send" should {
    "-- return Ok when sending a proper message" in {
      todo
    }

    "-- return Unauthorized when received an unknown token" in {
      todo
    }

    "-- return NotFound when received an unknown handle" in {
      todo
    }
  }

  "MessageController.receive" should {
    "-- eventually return Ok if there are no new messages" in {
      todo
    }

    "-- eventually return a received message" in {
      todo
    }

    "-- return Unauthorized when got an unknown token" in {
      todo
    }
  }

}
