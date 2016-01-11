package ru.org.codingteam.cttalk.services

import java.security.MessageDigest
import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.libs.concurrent.Execution.Implicits._
import ru.org.codingteam.cttalk.model.{Token, User}
import ru.org.codingteam.cttalk.services.messaging.SingleUserMessageReceiver

import scala.concurrent.Future

/**
 * Created by hgn on 20.10.2015.
 */

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {
  def createUser(name: String, password: String): Future[User]

  def auth(name: String, password: String): Future[Token]
}

class UserServiceImpl @Inject()(users: UserRepository, tokens: TokensRepository, messages: MessagesService) extends UserService {
  def createUser(name: String, password: String): Future[User] = {
    val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
    val hashedPassword: String = BigInt(digest.digest(password.getBytes("UTF-8"))).toString(16)
    val user = User(name, hashedPassword, Seq())
    users.save(user)
  }

  def auth(name: String, password: String): Future[Token] = {
    val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
    val sentPasswordHash: String = BigInt(digest.digest(password.getBytes("UTF-8"))).toString(16)

    users.getByNameAndPasswordHash(name, sentPasswordHash) flatMap {
      case None => Future.failed(new RuntimeException("invalid credentials"))
      case Some(user) => tokens.create(user) flatMap { token =>
        messages.register(token, new SingleUserMessageReceiver(token))
      }
    }
  }
}
