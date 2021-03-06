package ru.org.codingteam.cttalk.client.services

import com.greencatsoft.angularjs.core.HttpService
import com.greencatsoft.angularjs.{Factory, Service, injectable}
import ru.org.codingteam.cttalk.client.model.Handle
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport

/**
 * Created by hgn on 02.01.2016.
 */
@JSExport
@injectable("AuthenticationService")
class AuthenticationService(http: HttpService) extends Service {
  def register(name: String, password: String): Future[js.Any] = {
    http.post[js.Any]("/api/register", js.Dynamic.literal("name" -> name, "password" -> password))
  }

  def authenticate(name: String, password: String): Future[js.Any] = {
    http.post[js.Any]("/api/auth", js.Dynamic.literal("name" -> name, "password" -> password))
  }

  def self(): Future[Handle] = {
    http.get[js.Any]("/api/self")
      .map(JSON.stringify(_))
      .map(read[Handle])
  }
}

@JSExport
@injectable("AuthenticationService")
class AuthenticationServiceFactory(http: HttpService) extends Factory[AuthenticationService] {
  override def apply(): AuthenticationService = new AuthenticationService(http)
}
