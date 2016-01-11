package ru.org.codingteam.cttalk

import org.specs2.mock.Mockito
import play.api.libs.json.JsValue
import play.api.mvc.Cookie
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}
import ru.org.codingteam.cttalk.controllers.api.ChatController
import ru.org.codingteam.cttalk.model.{Token, User}
import ru.org.codingteam.cttalk.services.messaging.MessageReceiver
import ru.org.codingteam.cttalk.services.{MessagesService, TokensRepository, UserRepository}

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
    "-- respond with Unauthorized if got no token cookie at all" in {
      running(FakeApplication()) {
        val mockTokens = mock[TokensRepository]
        val token = mock[Token]
        mockTokens.get(anyString) returns Future.successful(Some(token))

        val mockMessages = mock[MessagesService]
        mockMessages.register(any[Token], any[MessageReceiver]) returns Future.successful(token)

        val eventualResult = new ChatController(mockTokens, mock[UserRepository])
          .chats(FakeRequest())

        status(eventualResult) mustEqual UNAUTHORIZED
      }
    }
  }
}
