package ru.org.codingteam.cttalk.services

import java.security.MessageDigest
import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.libs.concurrent.Execution.Implicits._
import reactivemongo.api.commands.WriteResult
import ru.org.codingteam.cttalk.models.User

import scala.concurrent.Future
import scala.util.Random

/**
 * Created by hgn on 20.10.2015.
 */

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {
  def createUser(name: String, password: String): Future[WriteResult]

  def auth(name: String, password: String): Future[Option[String]]
}

class UserServiceImpl @Inject()(users: UserRepository) extends UserService {
  def createUser(name: String, password: String): Future[WriteResult] = {
    val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
    val hashedPassword: String = BigInt(digest.digest(password.getBytes("UTF-8"))).toString(16)
    val user = User(name, hashedPassword, "")
    users.save(user)
  }

  def auth(name: String, password: String): Future[Option[String]] = {
    val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
    val sentPasswordHash: String = BigInt(digest.digest(password.getBytes("UTF-8"))).toString(16)
    val newToken = Random.nextLong().toHexString

    users.getByName(name).map(maybeUser => maybeUser.filter(user => user.passwordHash.equals(sentPasswordHash)))
      .flatMap({
      case None => Future.failed(new RuntimeException("Invalid username or password"))
      case Some(user) => users.setToken(user, newToken).map({ result =>
        if (result.ok) {
          Some(newToken)
        } else {
          None
        }
      })
    })
  }
}
