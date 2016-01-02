package ru.org.codingteam.cttalk.controllers.api

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Controller, Cookie}
import ru.org.codingteam.cttalk.model.Handle._
import ru.org.codingteam.cttalk.services.{TokensRepository, UserService}

/**
 * Created by hgn on 20.10.2015.
 */
class SecurityController @Inject()(userService: UserService, tokensRepository: TokensRepository) extends Controller with Secure with JsonRequest {

  implicit val reads = (JsPath \ 'name).read[String] and
    (JsPath \ 'password).read[String] tupled

  def register = httpsOnlyAsync(jsonAsync[(String, String)] {
    case (name, password) => userService.createUser(name, password) map { _ => Ok } recover {
      case _ => Unauthorized
    }
  })

  def auth = httpsOnlyAsync(jsonAsync[(String, String)] {
    case (name, password) => userService.auth(name, password) map { token =>
      Ok.withCookies(Cookie("token", token._id))
    } recover {
      case _ => Unauthorized
    }
  })

  def self = withAuthCookie("token") { cookie => { _ =>
    tokensRepository.get(cookie.value) map {
      case Some(token) => Ok(Json.toJson(token.handle))
      case None => Unauthorized
    }
  }
  }
}
