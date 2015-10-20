package ru.org.codingteam.cttalk.controllers.api

import play.api.libs.json.{Reads, JsError}
import play.api.mvc._

import scala.concurrent.Future

/**
 * Created by hgn on 20.10.2015.
 */
trait JsonRequest {

  def jsonAsync[T](f: T => Future[Result]) (implicit reads: Reads[T]) = {
    request: Request[AnyContent] =>
      request.body.asJson
        .map {
        json => json.validate[T]
          .map(f)
          .recoverTotal { e => Future.successful(Results.BadRequest(JsError.toJson(e))) }
      }.getOrElse(Future.successful(Results.BadRequest("Expecting JSON data")))
  }

  def json[T](f: T => Result) (implicit reads: Reads[T]) = { request: Request[AnyContent] =>
    request.body.asJson
      .map {
      json => json.validate[T]
        .map(f)
        .recoverTotal { e => Results.BadRequest(JsError.toJson(e)) }
    }.getOrElse(Results.BadRequest("Expecting JSON data"))
  }
}
