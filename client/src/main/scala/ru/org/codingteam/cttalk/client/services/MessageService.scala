package ru.org.codingteam.cttalk.client.services

import com.greencatsoft.angularjs.core.HttpPromise.promise2future
import com.greencatsoft.angularjs.core.HttpService
import com.greencatsoft.angularjs.{Factory, Service, injectable}
import prickle.{Pickle, Unpickle}
import ru.org.codingteam.cttalk.client.model.{ReceivedMessage, SentMessage}

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js
import scala.scalajs.js.Any.fromString
import scala.scalajs.js.JSON
import scala.util.{Failure, Success}

/**
 * Created by hgn on 10.11.2015.
 */
@injectable("messageService")
class MessageService(http: HttpService) extends Service {
  require(http != null, s"No http service supplied.")

  def send(message: SentMessage): Unit = {
    http.post[js.Any](s"/api/send", Pickle.intoString(message))
  }

  def receive(): Future[ReceivedMessage] = {
    http.get[js.Any]("/api/receive")
      .map(JSON.stringify(_))
      .map(Unpickle[ReceivedMessage].fromString(_))
      .flatMap {
        case Success(m) => Future.successful(m)
        case Failure(e) => Future.failed(e)
      }
  }
}

@injectable("messageService")
class MessageServiceFactory(http: HttpService) extends Factory[MessageService] {
  override def apply(): MessageService = new MessageService(http)
}
