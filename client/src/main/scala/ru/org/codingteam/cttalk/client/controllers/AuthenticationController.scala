package ru.org.codingteam.cttalk.client.controllers

import com.greencatsoft.angularjs.core.{HttpService, Location}
import com.greencatsoft.angularjs.{AbstractController, injectable}
import org.scalajs.dom.console
import ru.org.codingteam.cttalk.client.AuthenticationScope

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}

/**
 * Created by hgn on 23.11.2015.
 */
@JSExport
@injectable("AuthenticationController")
class AuthenticationController(scope: AuthenticationScope, http: HttpService, location: Location)
  extends AbstractController[AuthenticationScope](scope) {

  @JSExport
  def login = {
    http.post("/api/auth", js.Dynamic.literal("name" -> scope.login, "password" -> scope.password)).onComplete {
      case Success(_) => location.url("/")
      case Failure(error) => handleError(error)
    }

  }

  def handleError(error: Throwable): Unit = {
    console.error(s"Error while authenticating: $error")
  }
}
