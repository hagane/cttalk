package ru.org.codingteam.cttalk.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.ImplicitBSONHandlers._
import play.modules.reactivemongo.json.collection.JSONCollection
import ru.org.codingteam.cttalk.models.{Token, User}

import scala.concurrent.Future
import scala.util.Random

/**
 * Created by hgn on 25.10.2015.
 */
@ImplementedBy(classOf[TokensRepositoryImpl])
trait TokensRepository {
  def get(id: String): Future[Option[Token]]

  def create(user: User): Future[Token]
}

class TokensRepositoryImpl @Inject()(mongo: ReactiveMongoApi) extends TokensRepository {
  override def get(id: String): Future[Option[Token]] = {
    implicit val reads =
      ((JsPath \ "_id").read[String] and
        (JsPath \ "username").read[String])(Token.apply _)
    tokens.find(Json.obj("_id" -> id)).one[Token]
  }

  override def create(user: User): Future[Token] = {
    implicit val writes =
      ((JsPath \ "_id").write[String] and
        (JsPath \ "username").write[String])(unlift(Token.unapply))
    val nextId = Random.nextLong().toHexString //TODO replace this with injected secure-er generator
    val token = Token(nextId, user.name)
    tokens.insert(token) map { _ => token }
  }

  def tokens = mongo.db.collection[JSONCollection]("tokens")
}
