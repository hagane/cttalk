package ru.org.codingteam.cttalk

import java.util.Date

import org.specs2.mock.Mockito
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Cookie
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}
import ru.org.codingteam.cttalk.controllers.api.MessageController
import ru.org.codingteam.cttalk.model.{Handle, Message, Token, UserHandle}
import ru.org.codingteam.cttalk.services.{MessagesService, TokensRepository}

import scala.concurrent.Future

/**
 * Created by hgn on 29.10.2015.
 */
class MessageControllerSpec extends PlaySpecification with Mockito {
  sequential

  "MessageController.send" should {
    "-- return Ok when sending a proper message" in {
      running(FakeApplication()) {
        val mockMessages = mock[MessagesService]
        mockMessages.send(any[Message]) returns Future.successful(true)

        val mockTokens = mock[TokensRepository]
        mockTokens.get(anyString) returns Future.successful(Some(Token("valid", UserHandle("known"))))

        val controller = new MessageController(mockMessages, mockTokens)
        val eventualResponse = controller.send(FakeRequest().withJsonBody(
          Json.obj(
            "to" -> Json.obj(
              "user" -> "known"
            ),
            "text" -> "message text")).withCookies(
          Cookie("token", "valid")
        ))

        status(eventualResponse) mustEqual OK
      }
    }

    "-- return Unauthorized when received an invalid token cookie" in {
      running(FakeApplication()) {
        val mockMessages = mock[MessagesService]
        mockMessages.send(any[Message]) returns Future.successful(true)

        val mockTokens = mock[TokensRepository]
        mockTokens.get(anyString) returns Future.successful(None)

        val controller = new MessageController(mockMessages, mockTokens)
        val eventualResponse = controller.send(FakeRequest().withJsonBody(
          Json.obj(
            "to" -> Json.obj(
              "user" -> "known"
            ),
            "text" -> "message text")).withCookies(
          Cookie("token", "invalid")
        ))

        status(eventualResponse) mustEqual UNAUTHORIZED
      }
    }

    "-- return Unauthorized when received no token cookie at all" in {
      running(FakeApplication()) {
        val mockMessages = mock[MessagesService]
        mockMessages.send(any[Message]) returns Future.successful(true)

        val mockTokens = mock[TokensRepository]

        val controller = new MessageController(mockMessages, mockTokens)
        val eventualResponse = controller.send(FakeRequest().withJsonBody(
          Json.obj(
            "to" -> Json.obj(
              "user" -> "known"
            ),
            "text" -> "message text")))

        status(eventualResponse) mustEqual UNAUTHORIZED
      }
    }

    "-- return NotFound when received an unknown handle" in {
      running(FakeApplication()) {
        val mockMessages = mock[MessagesService]
        mockMessages.send(any[Message]) returns Future.successful(false)

        val mockTokens = mock[TokensRepository]
        mockTokens.getByHandle(any[Handle]) returns Future.successful(Seq())
        mockTokens.get(anyString) returns Future.successful(Some(Token("valid", UserHandle("known"))))

        val controller = new MessageController(mockMessages, mockTokens)
        val eventualResponse = controller.send(FakeRequest().withJsonBody(
          Json.obj("to" -> Json.obj(
            "user" -> "unknown"
          ),
            "text" -> "message text")).withCookies(
          Cookie("token", "valid")
        ))

        status(eventualResponse) mustEqual NOT_FOUND
      }
    }
  }

  "MessageController.receive" should {
    "-- eventually return Ok if there are no new messages" in {
      running(FakeApplication()) {
        val mockMessages = mock[MessagesService]
        mockMessages.get(any[Token]) returns Future.successful(Seq())

        val mockTokens = mock[TokensRepository]
        mockTokens.get(anyString) returns Future.successful(Some(Token("valid", UserHandle("known"))))

        val controller = new MessageController(mockMessages, mockTokens)

        val eventualResponse = controller.receive(FakeRequest().withCookies(
          Cookie("token", "valid")
        ))

        status(eventualResponse) mustEqual OK
      }
    }

    "-- eventually return a received message" in {
      val mockMessages = mock[MessagesService]
      mockMessages.get(any[Token]) returns Future.successful(Seq(Message(UserHandle(""), UserHandle(""), wasRead = false, new Date, "text")))

      val mockTokens = mock[TokensRepository]
      mockTokens.get(anyString) returns Future.successful(Some(Token("valid", UserHandle("known"))))

      val controller = new MessageController(mockMessages, mockTokens)

      val eventualResponse = controller.receive(FakeRequest().withCookies(
        Cookie("token", "valid")
      ))

      status(eventualResponse) mustEqual OK
      contentAsJson(eventualResponse) must beAnInstanceOf[JsValue]
    }

    "-- return Unauthorized when got an invalid token cookie" in {
      val mockTokens = mock[TokensRepository]
      mockTokens.get(anyString) returns Future.successful(None)

      val controller = new MessageController(mock[MessagesService], mockTokens)

      val eventualResponse = controller.receive(FakeRequest().withCookies(
        Cookie("token", "invalid")
      ))

      status(eventualResponse) mustEqual UNAUTHORIZED
    }

    "-- return Unauthorized when got no token cookie at all" in {
      val controller = new MessageController(mock[MessagesService], mock[TokensRepository])

      val eventualResponse = controller.receive(FakeRequest())

      status(eventualResponse) mustEqual UNAUTHORIZED
    }
  }

}
