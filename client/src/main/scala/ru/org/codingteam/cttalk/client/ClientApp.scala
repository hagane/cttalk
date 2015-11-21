package ru.org.codingteam.cttalk.client

import com.greencatsoft.angularjs.Angular
import ru.org.codingteam.cttalk.client.controllers.{ChatboxController, RosterController}
import ru.org.codingteam.cttalk.client.services.MessageServiceFactory

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

/**
 * Created by hgn on 10.11.2015.
 */
object ClientApp extends JSApp {
  @JSExport
  override def main(): Unit = {
    Angular.module("cttalk")
      .factory[MessageServiceFactory]
      .controller[RosterController]
      .controller[ChatboxController]
  }
}
