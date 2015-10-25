import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}
import reactivemongo.api.commands.WriteResult
import ru.org.codingteam.cttalk.controllers.api.SecurityController
import ru.org.codingteam.cttalk.services.UserService

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
        case Array("existing", _) => Future.successful(failureResult)
        case _ => Future.successful(successResult)
      }
    }

    service.auth(anyString, anyString) answers { args =>
      args match {
        case Array("existing", "correct") => Future.successful(Some("token"))
        case Array("existing", _) => Future.successful(None)
        case _ => Future.successful(None)
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
        val controller = new SecurityController(mockUserService)
        status(controller.register.apply(request)) mustEqual OK
      }
    }

    "-- return Unauthorized when user with given name exists" in {
      running(FakeApplication()) {
        val request = FakeRequest().withJsonBody(Json.obj(
          "name" -> "existing",
          "password" -> "password"
        ))
        val controller = new SecurityController(mockUserService)
        status(controller.register.apply(request)) mustEqual UNAUTHORIZED
      }
    }
  }

  "SecurityController.auth" should {
    "-- return OK(token) on successful auth" in {
      running(FakeApplication()) {
        val request = FakeRequest().withJsonBody(Json.obj(
          "name" -> "existing",
          "password" -> "correct"
        ))
        val controller = new SecurityController(mockUserService)
        val result: Future[Result] = controller.auth.apply(request)
        status(result) mustEqual OK
        contentAsJson(result) mustEqual Json.obj("token" -> "token")
      }
    }
    "-- return Unauthorized if user is not found" in {
      running(FakeApplication()) {
        val request = FakeRequest().withJsonBody(Json.obj(
          "name" -> "new",
          "password" -> "password"
        ))
        val controller = new SecurityController(mockUserService)
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
        val controller = new SecurityController(mockUserService)
        val result: Future[Result] = controller.auth.apply(request)
        status(result) mustEqual UNAUTHORIZED
      }
    }
  }
}
