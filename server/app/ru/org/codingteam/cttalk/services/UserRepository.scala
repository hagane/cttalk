package ru.org.codingteam.cttalk.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.ImplicitBSONHandlers._
import play.modules.reactivemongo.json.collection.JSONCollection
import ru.org.codingteam.cttalk.model.{Handle, Token, User, UserHandle}

import scala.concurrent.Future

/**
 * Created by hgn on 21.10.2015.
 */
@ImplementedBy(classOf[UserRepositoryImpl])
trait UserRepository {
  def getByToken(token: Token): Future[Option[User]]

  def getByNameAndPasswordHash(name: String, passwordHash: String): Future[Option[User]]

  def save(user: User): Future[User]

  def addToRoster(user: User, handle: Handle): Future[User]
}

class UserRepositoryImpl @Inject()(mongo: ReactiveMongoApi) extends UserRepository {

  implicit val reads = ((JsPath \ "name").read[String] and
    (JsPath \ "passwordHash").read[String] and
    (JsPath \ "roster").read[Seq[Handle]]) (User.apply _)

  override def getByNameAndPasswordHash(name: String, passwordHash: String): Future[Option[User]] = {
    users.find(Json.obj("name" -> name, "passwordHash" -> passwordHash)).one[User]
  }

  override def getByToken(token: Token): Future[Option[User]] = {
    token.handle match {
      case UserHandle(username) => users.find(Json.obj("name" -> username)).one[User]
      case _ => Future.failed(new RuntimeException("invalid token"))
    }
  }

  def users: JSONCollection = mongo.db.collection[JSONCollection]("users")

  override def save(user: User): Future[User] = {
    val jsonUser = Json.obj(
      "_id" -> user.name,
      "name" -> user.name,
      "passwordHash" -> user.passwordHash,
      "roster" -> Json.arr()
    )
    users.insert(jsonUser) map { _ => user }
  }

  override def addToRoster(user: User, handle: Handle): Future[User] = {
    users.update(
      Json.obj("_id" -> user.name),
      Json.obj("$push" -> Json.obj("roster" -> handle))
    ) flatMap {
      case result => users.find(Json.obj("_id" -> user.name)).one[User] flatMap {
        case Some(updatedUser) => Future.successful(updatedUser)
        case None => Future.failed(new RuntimeException)
      }
    }
  }
}
