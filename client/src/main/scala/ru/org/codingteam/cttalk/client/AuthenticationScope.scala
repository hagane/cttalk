package ru.org.codingteam.cttalk.client

import com.greencatsoft.angularjs.core.Scope

import scala.scalajs.js

/**
 * Created by hgn on 21.11.2015.
 */
@js.native
trait AuthenticationScope extends Scope {
  var login: String = js.native
  var password: String = js.native
}
