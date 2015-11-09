package ru.org.codingteam.cttalk.controllers

import play.api.mvc._
import ru.org.codingteam.cttalk._

/**
 * Created by hgn on 01.06.2015.
 */
class MainController extends Controller {

  def index = Action {
    Ok(views.html.main())
  }
}
