package ru.org.codingteam.cttalk.controllers.api

import com.google.inject.Inject
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import ru.org.codingteam.cttalk.model.Handle._
import ru.org.codingteam.cttalk.services.{TokensRepository, UserRepository}

import scala.concurrent.Future

/**
 * Created by hgn on 26.08.2015.
 */
class ChatController @Inject()(tokens: TokensRepository, users: UserRepository) extends Controller with Secure {

  def chats = withAuthCookie("token") { cookie => implicit request =>
    tokens.get(cookie.value) flatMap {
      case Some(token) => users.getByToken(token)
      case None => Future.successful(None)
    } map {
      case Some(user) => Ok(Json.toJson(user.roster))
      case None => Unauthorized
    }
  }
}
