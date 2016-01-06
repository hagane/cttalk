package ru.org.codingteam.cttalk

import org.specs2.mock.Mockito
import play.api.test.PlaySpecification

/**
 * Created by hgn on 06.01.2016.
 */
class MainControllerSpec extends PlaySpecification with Mockito {
  "MainController.index" should {
    "-- respond with html if got a valid token cookie" in {
      todo
    }
    "-- redirect to auth if token cookie is invalid" in {
      todo
    }
    "-- redirect to auth when got no token cookie at all" in {
      todo
    }
  }
}
