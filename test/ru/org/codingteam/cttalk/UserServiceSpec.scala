package ru.org.codingteam.cttalk

import java.security.MessageDigest

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock._
import play.api.test.PlaySpecification
import reactivemongo.api.commands.WriteResult
import ru.org.codingteam.cttalk.models.{Token, User}
import ru.org.codingteam.cttalk.services.{TokensRepository, UserRepository, UserServiceImpl}

import scala.concurrent.Future

/**
 * Created by hgn on 25.10.2015.
 */
class UserServiceSpec extends PlaySpecification with Mockito {
  sequential

  def mockTokensRepository = {
    val mockTokensRepository = mock[TokensRepository]
    mockTokensRepository.create(any[User]) answers { user =>
      Future.successful(Some(Token("token", user.asInstanceOf[User].name)))
    }
  }

  "UserService.createUser" should {
    "-- write user to db if there is no user with given name" in { implicit ee: ExecutionEnv =>
      val mockUserRepository = mock[UserRepository]
      val successfulResult = mock[WriteResult]
      successfulResult.ok returns true
      mockUserRepository.save(org.mockito.Matchers.any[User]) returns Future.successful(successfulResult)

      val service = new UserServiceImpl(mockUserRepository, mockTokensRepository)
      service.createUser("testname", "test") map {
        r => r.ok must be equalTo true
      } await
    }

    "-- fail when user with given name already exists" in { implicit ee: ExecutionEnv =>
      val mockUsersRepository = mock[UserRepository]
      val failedResult = mock[WriteResult]
      failedResult.ok returns false
      val successfulResult = mock[WriteResult]
      successfulResult.ok returns true
      val username = "testname"

      mockUsersRepository.save(org.mockito.Matchers.any[User]) answers { user =>
        val u = user.asInstanceOf[User]

        if (username.equals(u.name)) {
          Future.successful(failedResult)
        } else {
          Future.successful(successfulResult)
        }
      }

      val service = new UserServiceImpl(mockUsersRepository, mockTokensRepository)
      service.createUser(username, "test") map {
        r => r.ok must be equalTo false
      } await
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

      mockUsersRepository.getByName(anyString) returns Future.successful(Some(User("testname", hash)))

      val service = new UserServiceImpl(mockUsersRepository, mockTokensRepository)
      service.auth("testname", "testpassword") map { result => result must beSome[String] } await
    }

    "-- register token after successful authentication" in {
      skipped("todo")
    }

    "-- fail if user does not exists" in { implicit ee: ExecutionEnv =>
      val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
      val hash: String = BigInt(digest.digest("testpassword".getBytes("UTF-8"))).toString(16)
      val newToken = "token"
      val mockUsersRepository = mock[UserRepository]

      mockUsersRepository.getByName(anyString) returns Future.successful(None)

      val service = new UserServiceImpl(mockUsersRepository, mockTokensRepository)
      service.auth("testname", "testpassword") map { result => result must beNone } await
    }

    "-- fail if password does not matches" in { implicit ee: ExecutionEnv =>
      val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
      val hash: String = BigInt(digest.digest("testpassword".getBytes("UTF-8"))).toString(16)
      val newToken = "token"
      val mockUsersRepository = mock[UserRepository]

      mockUsersRepository.getByName(anyString) returns Future.successful(Some(User("testname", hash)))

      val service = new UserServiceImpl(mockUsersRepository, mockTokensRepository)
      service.auth("testname", "wrongpassword") map { result => result must beNone } await
    }
  }
}
