package ru.org.codingteam.cttalk.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import ru.org.codingteam.cttalk.models.{Token, User}

import scala.concurrent.Future

/**
 * Created by hgn on 21.10.2015.
 */
@ImplementedBy(classOf[UserRepositoryImpl])
trait UserRepository {
  def getByToken(token: Token): Future[Option[User]]

  def getByNameAndPasswordHash(name: String, passwordHash: String): Future[Option[User]]

  def save(user: User): Future[User]
}

class UserRepositoryImpl @Inject()(mongo: ReactiveMongoApi) extends UserRepository {

  implicit val reads = ((JsPath \ "name").read[String] and
    (JsPath \ "passwordHash").read[String])(User.apply _)

  override def getByNameAndPasswordHash(name: String, passwordHash: String): Future[Option[User]] = {
    users.find(Json.obj("name" -> name, "passwordHash" -> passwordHash)).one[User]
  }

  override def getByToken(token: Token): Future[Option[User]] = {
    users.find(Json.obj("name" -> token.username)).one[User]
  }

  override def save(user: User): Future[User] = {
    val jsonUser = Json.obj(
      "_id" -> user.name,
      "name" -> user.name,
      "passwordHash" -> user.passwordHash
    )
    users.insert(jsonUser) map { _ => user }
  }

  def users: JSONCollection = mongo.db.collection[JSONCollection]("users")
}
