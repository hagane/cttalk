package ru.org.codingteam.cttalk.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsObject, JsPath, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.api.libs.functional.syntax._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.commands.WriteResult
import ru.org.codingteam.cttalk.models.User

import scala.concurrent.Future

/**
 * Created by hgn on 21.10.2015.
 */
@ImplementedBy(classOf[UserRepositoryImpl])
trait UserRepository {
  def getByToken(token: String): Future[Option[User]]

  def getByName(name: String): Future[Option[User]]

  def save(user: User): Future[WriteResult]
  def setToken(user: User, token: String): Future[WriteResult]
}

class UserRepositoryImpl @Inject()(mongo: ReactiveMongoApi) extends UserRepository {

  implicit val reads = ((JsPath \ "name").read[String] and
    (JsPath \ "passwordHash").read[String] and
    (JsPath \ "token").read[String]) (User.apply _)

  override def getByName(name: String): Future[Option[User]] = {
    users.find(Json.obj("name" -> name)).one[User]
  }

  override def getByToken(token: String): Future[Option[User]] = {
    users.find(Json.obj("token" -> token)).one[User]
  }

  override def save(user: User): Future[WriteResult] = {
    val jsonUser = Json.obj(
      "_id" -> user.name.hashCode,
      "name" -> user.name,
      "passwordHash" -> user.passwordHash,
      "token" -> user.token
    )
    users.insert(jsonUser)
  }

  def users: JSONCollection = mongo.db.collection[JSONCollection]("users")

  override def setToken(user: User, token: String): Future[WriteResult] = {
    val selector: JsObject = Json.obj(
      "name" -> user.name,
      "token" -> user.token
    )
    val modifier: JsObject = Json.obj(
      "$set" -> Json.obj(
        "token" -> token
      )
    )

    users.update(selector, modifier)
  }
}
