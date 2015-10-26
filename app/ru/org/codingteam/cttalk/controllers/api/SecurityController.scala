package ru.org.codingteam.cttalk.controllers.api

import java.lang.RuntimeException
import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.Controller
import ru.org.codingteam.cttalk.services.UserService

/**
 * Created by hgn on 20.10.2015.
 */
class SecurityController @Inject()(userService: UserService) extends Controller with Secure with JsonRequest {

  implicit val reads = (JsPath \ 'name).read[String] and
    (JsPath \ 'password).read[String] tupled

  def register = httpsOnlyAsync(jsonAsync[(String, String)] {
    case (name, password) => userService.createUser(name, password)
      .map(result =>
      if (result.ok) {
        Ok("")
      } else {
        result.errmsg.map(msg => Unauthorized(msg)).getOrElse(InternalServerError(result.message))
      })
  })

  def auth = httpsOnlyAsync(jsonAsync[(String, String)] {
    case (name, password) => userService.auth(name, password) map { token =>
      Ok(Json.obj("token" -> token._id))
    } recover {
      case _ => Unauthorized
    }
  })
}
