package ru.org.codingteam.cttalk.services

import java.security.MessageDigest
import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.libs.concurrent.Execution.Implicits._
import reactivemongo.api.commands.WriteResult
import ru.org.codingteam.cttalk.models.{Message, MessageReceiver, Token, User}

import scala.concurrent.Future
import scala.util.Random

/**
 * Created by hgn on 20.10.2015.
 */

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {
  def createUser(name: String, password: String): Future[WriteResult]

  def auth(name: String, password: String): Future[Token]
}

class UserServiceImpl @Inject()(users: UserRepository, tokens: TokensRepository, messages: MessagesService) extends UserService {
  def createUser(name: String, password: String): Future[WriteResult] = {
    val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
    val hashedPassword: String = BigInt(digest.digest(password.getBytes("UTF-8"))).toString(16)
    val user = User(name, hashedPassword)
    users.save(user)
  }

  def auth(name: String, password: String): Future[Token] = {
    val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
    val sentPasswordHash: String = BigInt(digest.digest(password.getBytes("UTF-8"))).toString(16)

    val receiver = new MessageReceiver { //todo replace with something working
      override def receive(message: Message): Boolean = true
    }

    users.getByNameAndPasswordHash(name, sentPasswordHash) flatMap {
      case None => Future.failed(new RuntimeException("invalid credentials"))
      case Some(user) =>
        tokens.create(user) flatMap {
          case None => Future.failed(new RuntimeException("could not create a token"))
          case Some(token) => messages.register(token, receiver)
        }
    }
  }
}
