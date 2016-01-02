package ru.org.codingteam.cttalk.client.controllers

import com.greencatsoft.angularjs.{AbstractController, injectable}
import org.scalajs.dom.{console, window}
import ru.org.codingteam.cttalk.client.AuthenticationScope
import ru.org.codingteam.cttalk.client.services.AuthenticationService

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Success}

/**
 * Created by hgn on 23.11.2015.
 */
@JSExport
@injectable("AuthenticationController")
class AuthenticationController(scope: AuthenticationScope, auth: AuthenticationService)
  extends AbstractController[AuthenticationScope](scope) {

  @JSExport
  def login() = {
    auth.authenticate(scope.login, scope.password).onComplete {
      case Success(_) => redirectOnSuccess
      case Failure(error) => handleError(error)
    }
  }

  @JSExport
  def signUp() = {
    auth.register(scope.login, scope.password).onComplete {
      case Success(_) => redirectOnSuccess
      case Failure(error) => handleError(error)
    }
  }

  def handleError(error: Throwable): Unit = {
    console.error(s"Error while authenticating: $error")
  }

  def redirectOnSuccess = {
    window.location.href = "/"
  }
}
