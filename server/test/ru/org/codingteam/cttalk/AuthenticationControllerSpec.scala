package ru.org.codingteam.cttalk

import org.specs2.mock.Mockito
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}
import ru.org.codingteam.cttalk.controllers.AuthenticationController

/**
 * Created by hgn on 28.10.2015.
 */
class AuthenticationControllerSpec extends PlaySpecification with Mockito {
  sequential

  "AuthenticationController" should {

    "-- respond with html page" in {
      running(FakeApplication()) {
        val controller = new AuthenticationController()
        val eventualResponse = controller.main(FakeRequest())
        status(eventualResponse) mustEqual OK
        contentAsString(eventualResponse).isEmpty must beFalse
      }
    }
  }
}
