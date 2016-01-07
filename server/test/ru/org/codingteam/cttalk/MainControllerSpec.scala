package ru.org.codingteam.cttalk

import org.specs2.mock.Mockito
import play.api.mvc.Cookie
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}
import ru.org.codingteam.cttalk.controllers.MainController
import ru.org.codingteam.cttalk.model.Token
import ru.org.codingteam.cttalk.services.messaging.MessageReceiver
import ru.org.codingteam.cttalk.services.{MessagesService, TokensRepository}

import scala.concurrent.Future

/**
 * Created by hgn on 06.01.2016.
 */
class MainControllerSpec extends PlaySpecification with Mockito {
  sequential

  "MainController.index" should {
    "-- respond with html if got a valid token cookie" in {
      running(FakeApplication()) {
        val mockTokens = mock[TokensRepository]
        val token = mock[Token]
        mockTokens.get(anyString) returns Future.successful(Some(token))

        val mockMessages = mock[MessagesService]
        mockMessages.register(any[Token], any[MessageReceiver]) returns Future.successful(token)

        val eventualResult = new MainController(mockMessages, mockTokens)
          .index(FakeRequest().withCookies(Cookie("token", "valid")))

        status(eventualResult) mustEqual OK
        contentAsString(eventualResult).isEmpty must beFalse
      }
    }

    "-- redirect to auth if token cookie is invalid" in {
      running(FakeApplication()) {
        val mockTokens = mock[TokensRepository]
        val token = mock[Token]
        mockTokens.get(anyString) returns Future.successful(None)

        val mockMessages = mock[MessagesService]
        mockMessages.register(any[Token], any[MessageReceiver]) returns Future.successful(token)

        val eventualResult = new MainController(mockMessages, mockTokens)
          .index(FakeRequest().withCookies(Cookie("token", "invalid")))

        status(eventualResult) mustEqual TEMPORARY_REDIRECT
      }
    }
    "-- redirect to auth when got no token cookie at all" in {
      running(FakeApplication()) {
        val mockTokens = mock[TokensRepository]
        val token = mock[Token]
        mockTokens.get(anyString) returns Future.successful(Some(token))

        val mockMessages = mock[MessagesService]
        mockMessages.register(any[Token], any[MessageReceiver]) returns Future.successful(token)

        val eventualResult = new MainController(mockMessages, mockTokens)
          .index(FakeRequest())

        status(eventualResult) mustEqual TEMPORARY_REDIRECT
      }
    }
  }
}
