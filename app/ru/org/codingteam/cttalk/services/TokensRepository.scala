package ru.org.codingteam.cttalk.services

import javax.inject.Inject

import play.api.libs.json.{JsPath, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection
import ru.org.codingteam.cttalk.models.{User, Token}
import play.api.libs.functional.syntax._

import scala.concurrent.Future
import scala.util.Random

/**
 * Created by hgn on 25.10.2015.
 */
trait TokensRepository {
  def get(id: String): Future[Option[Token]]
  def create(user: User): Future[Option[Token]]
}

class TokensRepositoryImpl @Inject() (mongo: ReactiveMongoApi) extends TokensRepository {
  def tokens = mongo.db.collection[JSONCollection]("tokens")

  override def get(id: String): Future[Option[Token]] = {
    implicit val reads =
      ((JsPath \ "_id").read[String] and
      (JsPath \ "username").read[String]) (Token.apply _)
    tokens.find(Json.obj("_id" -> id)).one[Token]
  }

  override def create(user: User): Future[Option[Token]] = {
    implicit val writes =
      ((JsPath \ "_id").write[String] and
      (JsPath \ "username").write[String]) (unlift(Token.unapply))
    val nextId = Random.nextLong().toHexString //TODO replace this with injected secure-er generator
    val token = Token(nextId, user.name)
    tokens.insert(tokens) map { maybeResult =>
      if(maybeResult.ok) {
        Some(token)
      } else {
        None
      }
    }
  }
}
