package ru.org.codingteam.cttalk.client.services

import com.greencatsoft.angularjs.core.HttpPromise.promise2future
import com.greencatsoft.angularjs.core.HttpService
import com.greencatsoft.angularjs.{Factory, Service, injectable}
import ru.org.codingteam.cttalk.client.model.{ReceivedMessage, SentMessage}
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js
import scala.scalajs.js.Any.fromString
import scala.scalajs.js.JSON

/**
 * Created by hgn on 10.11.2015.
 */
@injectable("MessageService")
class MessageService(http: HttpService) extends Service {
  require(http != null, "No http service supplied.")

  def send(message: SentMessage): Unit = {
    http.post[js.Any]("/api/send", write(message))
  }

  def receive(): Future[Seq[ReceivedMessage]] = {
    http.get[js.Any]("/api/receive")
      .map(JSON.stringify(_))
      .map(read[Seq[ReceivedMessage]])
  }
}

@injectable("MessageService")
class MessageServiceFactory(http: HttpService) extends Factory[MessageService] {
  override def apply(): MessageService = new MessageService(http)
}
