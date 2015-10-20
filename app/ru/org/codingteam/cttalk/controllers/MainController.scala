package ru.org.codingteam.cttalk.controllers

import ru.org.codingteam.cttalk._

import play.api.mvc._

/**
 * Created by hgn on 01.06.2015.
 */
class MainController extends Controller {

  def index = Action {
    Ok(views.html.main())
  }
}
