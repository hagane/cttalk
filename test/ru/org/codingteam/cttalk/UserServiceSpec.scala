package ru.org.codingteam.cttalk

import java.security.MessageDigest

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock._
import play.api.test.PlaySpecification
import reactivemongo.api.commands.WriteResult
import ru.org.codingteam.cttalk.models.{Token, User}
import ru.org.codingteam.cttalk.services.messaging.MessageReceiver
import ru.org.codingteam.cttalk.services.{MessagesService, TokensRepository, UserRepository, UserServiceImpl}

import scala.concurrent.Future

/**
 * Created by hgn on 25.10.2015.
 */
class UserServiceSpec extends PlaySpecification with Mockito {
  sequential

  def mockTokensRepository = {
    val mockRepository = mock[TokensRepository]
    mockRepository.create(any[User]) answers { user =>
      Future.successful(Token("token", user.asInstanceOf[User].name))
    }

    mockRepository
  }

  def mockMessagesService = {
    val mockService = mock[MessagesService]
    mockService.register(any[Token], any[MessageReceiver]) answers {args =>
      args match {
        case Array(token, _) => Future.successful(token.asInstanceOf[Token])
      }
    }

    mockService
  }

  "UserService.createUser" should {
    "-- write user to db if there is no user with given name" in { implicit ee: ExecutionEnv =>
      val mockUserRepository = mock[UserRepository]
      mockUserRepository.save(org.mockito.Matchers.any[User]) returns Future.successful(User("", ""))

      val service = new UserServiceImpl(mockUserRepository, mockTokensRepository, mockMessagesService)
      service.createUser("testname", "test") map { user => user must beAnInstanceOf[User] } await
    }

    "-- fail when user with given name already exists" in { implicit ee: ExecutionEnv =>
      val mockUsersRepository = mock[UserRepository]
      val username = "testname"

      mockUsersRepository.save(org.mockito.Matchers.any[User]) answers { user =>
        val u = user.asInstanceOf[User]

        if (username.equals(u.name)) {
          Future.failed(new RuntimeException)
        } else {
          Future.successful(u)
        }
      }

      val service = new UserServiceImpl(mockUsersRepository, mockTokensRepository, mockMessagesService)
      service.createUser(username, "test") must throwA[Exception].await
    }
  }

  "UserService.auth" should {
    "-- return auth token if auth is successful" in { implicit ee: ExecutionEnv =>
      val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
      val hash: String = BigInt(digest.digest("testpassword".getBytes("UTF-8"))).toString(16)
      val newToken = "token"
      val mockUsersRepository = mock[UserRepository]
      val successfulResult = mock[WriteResult]
      successfulResult.ok returns true

      mockUsersRepository.getByNameAndPasswordHash(anyString, anyString) returns Future.successful(Some(User("testname", hash)))

      val service = new UserServiceImpl(mockUsersRepository, mockTokensRepository, mockMessagesService)
      service.auth("testname", "testpassword") map { result => result must beAnInstanceOf[Token] } await
    }

    "-- register token after successful authentication" in { implicit ee: ExecutionEnv =>
      val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
      val hash: String = BigInt(digest.digest("testpassword".getBytes("UTF-8"))).toString(16)
      val newToken = "token"
      val mockUsersRepository = mock[UserRepository]
      val successfulResult = mock[WriteResult]
      successfulResult.ok returns true

      mockUsersRepository.getByNameAndPasswordHash(anyString, anyString) returns Future.successful(Some(User("testname", hash)))
      val messagesService = mockMessagesService
      val service = new UserServiceImpl(mockUsersRepository, mockTokensRepository, messagesService)
      service.auth("testname", "testpassword") map { result => result must beAnInstanceOf[Token] } await

      there was one(messagesService).register(any[Token], any[MessageReceiver])
    }

    "-- fail if user does not exists" in { implicit ee: ExecutionEnv =>
      val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
      val hash: String = BigInt(digest.digest("testpassword".getBytes("UTF-8"))).toString(16)
      val newToken = "token"
      val mockUsersRepository = mock[UserRepository]

      mockUsersRepository.getByNameAndPasswordHash(anyString, anyString) returns Future.successful(None)

      val service = new UserServiceImpl(mockUsersRepository, mockTokensRepository, mockMessagesService)
      service.auth("testname", "testpassword") must throwA[Exception].await
    }

    "-- fail if password does not match" in { implicit ee: ExecutionEnv =>
      val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
      val hash: String = BigInt(digest.digest("testpassword".getBytes("UTF-8"))).toString(16)
      val newToken = "token"
      val mockUsersRepository = mock[UserRepository]

      mockUsersRepository.getByNameAndPasswordHash(anyString, anyString) returns Future.successful(Some(User("testname", hash)))

      val service = new UserServiceImpl(mockUsersRepository, mockTokensRepository, mockMessagesService)
      service.auth("testname", "wrongpassword") must throwA[Exception].await
    }
  }
}
