package ru.org.codingteam.cttalk.controllers

import play.api.mvc._
import ru.org.codingteam.cttalk._
import ru.org.codingteam.cttalk.controllers.api.Secure

/**
 * Created by hgn on 23.11.2015.
 */
class AuthenticationController extends Controller with Secure {

  def main = httpsOnly { _ => Ok(views.html.auth()) }

}
