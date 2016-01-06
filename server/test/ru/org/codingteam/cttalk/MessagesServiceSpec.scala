package ru.org.codingteam.cttalk

import java.util.Date

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import play.api.test.PlaySpecification
import ru.org.codingteam.cttalk.model.{Handle, Message, Token, UserHandle}
import ru.org.codingteam.cttalk.services.messaging.MessageReceiver
import ru.org.codingteam.cttalk.services.{MessagesRepository, MessagesServiceImpl, TokensRepository}

import scala.concurrent.{Future, Promise}

/**
 * Created by hgn on 25.10.2015.
 */
class MessagesServiceSpec extends PlaySpecification with Mockito {
  sequential

  def mockMessagesRepository = {
    val repository = mock[MessagesRepository]
    repository.save(any[Message]) answers {
      message => Future.successful(message.asInstanceOf[Message])
    }

    repository
  }

  private class MockReceiver(receiverToken: Token) extends MessageReceiver {
    private val list = List.newBuilder[Message]

    override def receive(message: Message): Boolean = {
      list += message
      true
    }

    def get(): Promise[Seq[Message]] = Promise.successful(list.result())

    override def token(): Token = receiverToken
  }

  "MessageService.send" should {
    "-- save message in repository and succeed when sending to existing recipient" in { implicit ee: ExecutionEnv =>
      val repository = mockMessagesRepository

      val token: Token = Token("existing", UserHandle("username"))
      val mockTokens = mock[TokensRepository]
      mockTokens.getByHandle(any[UserHandle]) returns Future.successful(Seq(token))

      val service = new MessagesServiceImpl(repository, mockTokens)
      service.register(token, new MockReceiver(mock[Token]))
      service.send(Message(UserHandle("sender"), UserHandle("existing"), wasRead = false, new Date, "message")) map {
        result => result mustEqual true
      } await

      there was one(repository).save(any[Message])
    }

    "-- fail when sending to unknown recipient" in { implicit ee: ExecutionEnv =>
      val mockTokens = mock[TokensRepository]
      mockTokens.getByHandle(any[UserHandle]) returns Future.successful(Seq())
      val service = new MessagesServiceImpl(mockMessagesRepository, mockTokens)
      service.send(Message(UserHandle("sender"), UserHandle("receiver"), wasRead = false, new Date, "message")) must throwA[Exception].await
    }
  }

  "MessageService.register" should {
    "-- register recipient if token is not previously registered" in {
      success("tested in other tests")
    }

    "-- return existing token if recipient is already registered" in { implicit ee: ExecutionEnv =>
      val service = new MessagesServiceImpl(mockMessagesRepository, mock[TokensRepository])
      val token: Token = Token("receiver", UserHandle("username"))
      val token2: Token = Token("receiver", UserHandle("username2"))
      service.register(token, new MockReceiver(token)) map { _ => success } await

      service.register(token, new MockReceiver(token2)) map {
        _ mustEqual token
      } await
    }
  }

  "MessageService.get" should {
    "-- eventually return empty Seq if there are no new messages" in { implicit ee: ExecutionEnv =>
      val mockTokens = mock[TokensRepository]

      val token = Token("valid", UserHandle("user"))
      mockTokens.getByHandle(any[Handle]) returns Future.successful(Seq(token))
      val service = new MessagesServiceImpl(mock[MessagesRepository], mockTokens)

      val receiver = new MockReceiver(mock[Token])

      service.register(token, receiver)
      service.get(Token("valid", UserHandle("user"))) map {
        case Seq() => success
        case _ => failure("not an empty Seg")
      } await
    }

    "-- eventually return a Seq of received messages" in { implicit ee: ExecutionEnv =>
      val mockTokens = mock[TokensRepository]

      val token = Token("valid", UserHandle("user"))
      mockTokens.getByHandle(any[Handle]) returns Future.successful(Seq(token))
      val service = new MessagesServiceImpl(mock[MessagesRepository], mockTokens)

      val receiver = new MockReceiver(token)
      receiver.receive(mock[Message])

      service.register(token, receiver)
      service.get(Token("valid", UserHandle("user"))) map {
        case Seq() => failure("empty seq")
        case Seq(_) => success
        case _ => failure("not a Seg")
      } await
    }

    "-- eventually fail if got an unknown handle" in { implicit ee: ExecutionEnv =>
      val mockTokens = mock[TokensRepository]

      val token = Token("valid", UserHandle("user"))
      mockTokens.getByHandle(any[Handle]) returns Future.successful(Seq())
      val service = new MessagesServiceImpl(mock[MessagesRepository], mockTokens)

      val receiver = new MockReceiver(token)

      service.register(token, receiver)
      service.get(Token("invalid", UserHandle("user"))) must throwA[RuntimeException].await
    }
  }
}
