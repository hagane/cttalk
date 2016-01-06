package ru.org.codingteam.cttalk.controllers

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import ru.org.codingteam.cttalk._
import ru.org.codingteam.cttalk.services.messaging.SingleUserMessageReceiver
import ru.org.codingteam.cttalk.services.{MessagesService, TokensRepository}

import scala.concurrent.Future

/**
 * Created by hgn on 01.06.2015.
 */
class MainController @Inject()(messages: MessagesService, tokens: TokensRepository) extends Controller {

  def index = Action.async { implicit request: Request[AnyContent] =>
    (request.cookies.get("token") map {
      cookie => tokens.get(cookie.value)
    } getOrElse Future.successful(None)) map {
      case Some(token) => {
        messages.register(token, new SingleUserMessageReceiver(token))
        Ok(views.html.main())
      }
      case None => Redirect(routes.AuthenticationController.main(), TEMPORARY_REDIRECT)
    }
  }
}
