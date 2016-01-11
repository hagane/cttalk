package ru.org.codingteam.cttalk

import org.specs2.mock.Mockito
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Cookie
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}
import ru.org.codingteam.cttalk.controllers.api.ChatController
import ru.org.codingteam.cttalk.model.{Token, User}
import ru.org.codingteam.cttalk.services.{TokensRepository, UserRepository}

import scala.concurrent.Future

/**
 * Created by hgn on 11.01.2016.
 */
class ChatControllerSpec extends PlaySpecification with Mockito {
  sequential

  "ChatController.chats" should {
    "-- respond with json if got a valid token cookie" in {
      running(FakeApplication()) {
        val mockTokens = mock[TokensRepository]
        val token = mock[Token]
        mockTokens.get(anyString) returns Future.successful(Some(token))

        val mockUsers = mock[UserRepository]
        mockUsers.getByToken(any[Token]) returns Future.successful(Some(User("user", "asdf", Seq())))

        val eventualResult = new ChatController(mockTokens, mockUsers)
          .chats(FakeRequest().withCookies(Cookie("token", "valid")))

        status(eventualResult) mustEqual OK
        contentAsJson(eventualResult) must beAnInstanceOf[JsValue]
      }
    }

    "-- respond with Unauthorized if token cookie is invalid" in {
      running(FakeApplication()) {
        val mockTokens = mock[TokensRepository]
        val token = mock[Token]
        mockTokens.get(anyString) returns Future.successful(None)

        val eventualResult = new ChatController(mockTokens, mock[UserRepository])
          .chats(FakeRequest().withCookies(Cookie("token", "invalid")))

        status(eventualResult) mustEqual UNAUTHORIZED
      }
    }

    "-- respond woth NotFound if token is valid but user is not found" in {
      val mockTokens = mock[TokensRepository]
      val token = mock[Token]
      mockTokens.get(anyString) returns Future.successful(Some(token))

      val mockUsers = mock[UserRepository]
      mockUsers.getByToken(any[Token]) returns Future.successful(None)
      val eventualResult = new ChatController(mockTokens, mockUsers)
        .chats(FakeRequest().withCookies(Cookie("token", "invalid")))

      status(eventualResult) mustEqual NOT_FOUND
    }

    "-- respond with Unauthorized if got no token cookie at all" in {
      running(FakeApplication()) {
        val mockTokens = mock[TokensRepository]
        val token = mock[Token]
        mockTokens.get(anyString) returns Future.successful(Some(token))

        val eventualResult = new ChatController(mockTokens, mock[UserRepository])
          .chats(FakeRequest())

        status(eventualResult) mustEqual UNAUTHORIZED
      }
    }
  }

  "ChatController.add" should {
    "-- respond with Ok if got a valid token cookie and a valid Handle" in {
      running(FakeApplication()) {
        val mockTokens = mock[TokensRepository]
        val token = mock[Token]
        mockTokens.get(anyString) returns Future.successful(Some(token))

        val mockUsers = mock[UserRepository]
        mockUsers.getByToken(any[Token]) returns Future.successful(Some(User("user", "asdf", Seq())))

        val eventualResult = new ChatController(mockTokens, mockUsers)
          .add(FakeRequest()
            .withCookies(Cookie("token", "valid"))
            .withJsonBody(Json.obj("user" -> "some_username")))

        status(eventualResult) mustEqual OK
      }
    }

    "-- respond with Unauthorized if token cookie is invalid" in {
      running(FakeApplication()) {
        val mockTokens = mock[TokensRepository]
        val token = mock[Token]
        mockTokens.get(anyString) returns Future.successful(None)

        val eventualResult = new ChatController(mockTokens, mock[UserRepository])
          .chats(FakeRequest()
            .withCookies(Cookie("token", "invalid"))
            .withJsonBody(Json.obj("user" -> "some_username")))

        status(eventualResult) mustEqual UNAUTHORIZED
      }
    }

    "-- respond woth NotFound if token is valid but user is not found" in {
      val mockTokens = mock[TokensRepository]
      val token = mock[Token]
      mockTokens.get(anyString) returns Future.successful(Some(token))

      val mockUsers = mock[UserRepository]
      mockUsers.getByToken(any[Token]) returns Future.successful(None)
      val eventualResult = new ChatController(mockTokens, mockUsers)
        .chats(FakeRequest()
          .withCookies(Cookie("token", "invalid"))
          .withJsonBody(Json.obj("user" -> "some_username")))

      status(eventualResult) mustEqual NOT_FOUND
    }

    "-- respond with Unauthorized if got no token cookie at all" in {
      running(FakeApplication()) {
        val mockTokens = mock[TokensRepository]
        val token = mock[Token]
        mockTokens.get(anyString) returns Future.successful(Some(token))

        val eventualResult = new ChatController(mockTokens, mock[UserRepository])
          .chats(FakeRequest()
            .withJsonBody(Json.obj("user" -> "some_username")))

        status(eventualResult) mustEqual UNAUTHORIZED
      }
    }

    "-- respond with Bad Request if no handle is present" in {
      running(FakeApplication()) {
        val mockTokens = mock[TokensRepository]
        val token = mock[Token]
        mockTokens.get(anyString) returns Future.successful(Some(token))

        val mockUsers = mock[UserRepository]
        mockUsers.getByToken(any[Token]) returns Future.successful(Some(User("user", "asdf", Seq())))

        val eventualResult = new ChatController(mockTokens, mockUsers)
          .add(FakeRequest()
            .withCookies(Cookie("token", "valid")))

        status(eventualResult) mustEqual BAD_REQUEST
      }
    }
  }
}
