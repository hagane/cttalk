package ru.org.codingteam.cttalk.controllers.api

import play.Play
import play.api.mvc._

import scala.concurrent.Future

/**
 * Created by hgn on 20.10.2015.
 */
trait Secure {

  def httpsOnly(f: Request[AnyContent] => Result) = Action { implicit request =>
    if (Play.isProd && !request.secure) {
      Results.Status(418)
    } else {
      f(request)
    }
  }

  def httpsOnlyAsync(f: Request[AnyContent] => Future[Result]) = Action.async { implicit request =>
    if (Play.isProd && !request.secure) {
      Future.successful(Results.Status(418))
    } else {
      f(request)
    }
  }

  def withAuthCookie(cookieName: String)(f: Cookie => Request[AnyContent] => Future[Result]) = Action.async { implicit request =>
    request.cookies.get(cookieName) map {
      f(_)(request)
    } getOrElse Future.successful(Results.Unauthorized)
  }
}
