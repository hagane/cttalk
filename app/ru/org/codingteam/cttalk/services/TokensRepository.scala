package ru.org.codingteam.cttalk.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.ImplicitBSONHandlers._
import play.modules.reactivemongo.json.collection.JSONCollection
import ru.org.codingteam.cttalk.models.Handle._
import ru.org.codingteam.cttalk.models.{Handle, Token, User, UserHandle}

import scala.concurrent.Future
import scala.util.Random

/**
 * Created by hgn on 25.10.2015.
 */
@ImplementedBy(classOf[TokensRepositoryImpl])
trait TokensRepository {
  def get(id: String): Future[Option[Token]]

  def getByHandle(handle: Handle): Future[Seq[Token]]

  def create(user: User): Future[Token]
}

class TokensRepositoryImpl @Inject()(mongo: ReactiveMongoApi) extends TokensRepository {

  implicit val reads =
    ((JsPath \ "_id").read[String] and
        (JsPath \ "handle").read[Handle])(Token.apply _)

  implicit val writes =
    ((JsPath \ "_id").write[String] and
        (JsPath \ "handle").write[Handle])(unlift(Token.unapply))

  override def get(id: String): Future[Option[Token]] = {
    tokens.find(Json.obj("_id" -> id)).one[Token]
  }

  override def create(user: User): Future[Token] = {
    val nextId = Random.nextLong().toHexString //TODO replace this with injected secure-er generator
    val token = Token(nextId, UserHandle(user.name))
    tokens.insert(token) map { _ => token }
  }

  def tokens = mongo.db.collection[JSONCollection]("tokens")

  override def getByHandle(handle: Handle): Future[Seq[Token]] = {
    tokens.find(Json.obj("handle" -> Json.toJson(handle))).cursor[Token]().collect[Seq]()
  }
}
