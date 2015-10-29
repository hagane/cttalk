package ru.org.codingteam.cttalk

import play.api.libs.json.{JsValue, Json}
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}
import ru.org.codingteam.cttalk.controllers.api.MessageController

/**
 * Created by hgn on 29.10.2015.
 */
class MessageControllerSpec extends PlaySpecification {
  "MessageController.send" should {
    "-- return Ok when sending a proper message" in {
      running(FakeApplication()) {
        val controller = new MessageController
        val eventualResponse = controller.send(FakeRequest().withJsonBody(
          Json.obj(
            "token" -> "valid",
            "to" -> Json.obj(
              "user" -> "known"
            ),
            "text" -> "message text")))

        status(eventualResponse) mustEqual OK
      }
    }

    "-- return Unauthorized when received an unknown token" in {
      running(FakeApplication()) {
        val controller = new MessageController
        val eventualResponse = controller.send(FakeRequest().withJsonBody(
          Json.obj(
            "token" -> "invalid",
            "to" -> Json.obj(
              "user" -> "known"
            ),
            "text" -> "message text")))

        status(eventualResponse) mustEqual UNAUTHORIZED
      }
    }

    "-- return NotFound when received an unknown handle" in {
      running(FakeApplication()) {
        val controller = new MessageController
        val eventualResponse = controller.send(FakeRequest().withJsonBody(
          Json.obj(
            "token" -> "valid",
            "to" -> Json.obj(
              "user" -> "unknown"
            ),
            "text" -> "message text")))

        status(eventualResponse) mustEqual NOT_FOUND
      }
    }
  }

  "MessageController.receive" should {
    "-- eventually return Ok if there are no new messages" in {
      running(FakeApplication()) {
        val controller = new MessageController
        val eventualResponse = controller.send(FakeRequest().withJsonBody(
          Json.obj("token" -> "valid")))

        status(eventualResponse) mustEqual OK
      }
    }

    "-- eventually return a received message" in {
      val controller = new MessageController
      controller.send(FakeRequest().withJsonBody(
        Json.obj(
          "token" -> "valid",
          "to" -> Json.obj(
            "user" -> "known"
          ),
          "text" -> "message text")))

      val eventualResponse = controller.send(FakeRequest().withJsonBody(
        Json.obj("token" -> "valid")))

      status(eventualResponse) mustEqual OK
      contentAsJson(eventualResponse) must beAnInstanceOf[JsValue]
    }

    "-- return Unauthorized when got an unknown token" in {
      val controller = new MessageController

      val eventualResponse = controller.send(FakeRequest().withJsonBody(
        Json.obj("token" -> "invalid")))

      status(eventualResponse) mustEqual UNAUTHORIZED
    }
  }

}
