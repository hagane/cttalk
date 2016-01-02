package ru.org.codingteam.cttalk.controllers

import play.api.mvc._
import ru.org.codingteam.cttalk._

/**
 * Created by hgn on 01.06.2015.
 */
class MainController extends Controller {

  def index = Action { implicit request: Request[AnyContent] =>
    request.cookies.get("token") map {
      _ => Ok(views.html.main())
    } getOrElse Redirect(routes.AuthenticationController.main(), TEMPORARY_REDIRECT)
  }
}
