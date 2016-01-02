package ru.org.codingteam.cttalk

import org.specs2.mock.Mockito
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Cookie, Result}
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}
import reactivemongo.api.commands.WriteResult
import ru.org.codingteam.cttalk.controllers.api.SecurityController
import ru.org.codingteam.cttalk.model.{Token, User, UserHandle}
import ru.org.codingteam.cttalk.services.{TokensRepository, UserService}

import scala.concurrent.Future

/**
 * Created by hgn on 25.10.2015.
 */
class SecurityControllerSpec extends PlaySpecification with Mockito {
  sequential

  def mockUserService = {
    val service = mock[UserService]
    val successResult = mock[WriteResult]
    successResult.ok returns true

    val failureResult = mock[WriteResult]
    failureResult.ok returns false
    failureResult.errmsg returns Some("error message")

    service.createUser(anyString, anyString) answers { args =>
      args match {
        case Array("existing", _) => Future.failed(new Throwable)
        case _ => Future.successful(User("", ""))
      }
    }

    service.auth(anyString, anyString) answers { args =>
      args match {
        case Array("existing", "correct") => Future.successful(Token("token", UserHandle("existing")))
        case Array("existing", _) => Future.failed(new RuntimeException)
        case _ => Future.failed(new RuntimeException)
      }
    }
  }

  "SecurityController.createUser" should {
    "-- return Ok when trying to create new user" in {
      running(FakeApplication()) {
        val request = FakeRequest().withJsonBody(Json.obj(
          "name" -> "new",
          "password" -> "password"
        ))
        val controller = new SecurityController(mockUserService, mock[TokensRepository])
        status(controller.register.apply(request)) mustEqual OK
      }
    }

    "-- return Unauthorized when user with given name exists" in {
      running(FakeApplication()) {
        val request = FakeRequest().withJsonBody(Json.obj(
          "name" -> "existing",
          "password" -> "password"
        ))
        val controller = new SecurityController(mockUserService, mock[TokensRepository])
        status(controller.register.apply(request)) mustEqual UNAUTHORIZED
      }
    }
  }

  "SecurityController.auth" should {
    "-- return OK with a cookie on successful auth" in {
      running(FakeApplication()) {
        val request = FakeRequest().withJsonBody(Json.obj(
          "name" -> "existing",
          "password" -> "correct"
        ))
        val controller = new SecurityController(mockUserService, mock[TokensRepository])
        val result: Future[Result] = controller.auth.apply(request)
        status(result) mustEqual OK
        cookies(result).get("token") must not be empty
      }
    }

    "-- return Unauthorized if user is not found" in {
      running(FakeApplication()) {
        val request = FakeRequest().withJsonBody(Json.obj(
          "name" -> "new",
          "password" -> "password"
        ))
        val controller = new SecurityController(mockUserService, mock[TokensRepository])
        val result: Future[Result] = controller.auth.apply(request)
        status(result) mustEqual UNAUTHORIZED
      }
    }
    "-- return Unauthorized if password is incorrect" in {
      running(FakeApplication()) {
        val request = FakeRequest().withJsonBody(Json.obj(
          "name" -> "existing",
          "password" -> "password"
        ))
        val controller = new SecurityController(mockUserService, mock[TokensRepository])
        val result: Future[Result] = controller.auth.apply(request)
        status(result) mustEqual UNAUTHORIZED
      }
    }
  }

  "SecurityController.self" should {
    "-- return Ok with handle if given a valid token" in {
      running(FakeApplication()) {
        val request = FakeRequest().withCookies(Cookie("token", "valid"))
        val mockTokensRepository = mock[TokensRepository]
        mockTokensRepository.get(anyString) returns Future.successful(Some(Token("id", UserHandle("user"))))

        val controller = new SecurityController(mock[UserService], mockTokensRepository)
        val eventualResult = controller.self.apply(request)
        status(eventualResult) mustEqual OK
        contentAsJson(eventualResult) must beAnInstanceOf[JsValue]
      }
    }
    "-- return Unauthorized if given an invalid token" in {
      running(FakeApplication()) {
        val request = FakeRequest().withCookies(Cookie("token", "invalid"))
        val mockTokensRepository = mock[TokensRepository]
        mockTokensRepository.get(anyString) returns Future.successful(None)

        val controller = new SecurityController(mock[UserService], mockTokensRepository)
        val eventualResult = controller.self.apply(request)
        status(eventualResult) mustEqual UNAUTHORIZED
      }
    }
    "-- return Unauthorized if given no token at all" in {
      val request = FakeRequest()
      val controller = new SecurityController(mock[UserService], mock[TokensRepository])
      val eventualResult = controller.self.apply(request)
      status(eventualResult) mustEqual UNAUTHORIZED
    }
  }
}
