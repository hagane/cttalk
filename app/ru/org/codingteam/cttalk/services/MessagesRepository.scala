package ru.org.codingteam.cttalk.services

import java.util.Date
import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.ImplicitBSONHandlers._
import play.modules.reactivemongo.json.collection.JSONCollection
import ru.org.codingteam.cttalk.models.Handle._
import ru.org.codingteam.cttalk.models.{Handle, Message}

import scala.concurrent.Future

/**
 * Created by hgn on 25.10.2015.
 */
@ImplementedBy(classOf[MessagesRepositoryImpl])
trait MessagesRepository {
  def save(message: Message): Future[Message]

  def getUnread(handle: Handle): Future[Seq[Message]]

  def getLast(sender: Handle, receiver: Handle, upTo: Int = Int.MaxValue): Future[Seq[Message]]

  def markRead(messageSeq: Seq[Message]): Future[Seq[Message]]
}

class MessagesRepositoryImpl @Inject()(mongo: ReactiveMongoApi, tokens: TokensRepository) extends MessagesRepository {

  implicit val writes = ((JsPath \ "_id").write[String] and
      (JsPath \ "sender").write[Handle] and
      (JsPath \ "receiver").write[Handle] and
      (JsPath \ "wasRead").write[Boolean] and
      (JsPath \ "moment").write[Date] and
      (JsPath \ "text").write[String]) {m: Message => (m.id, m.sender, m.receiver, m.wasRead, m.moment, m.text)}

  implicit val reads = ((JsPath \ "sender").read[Handle] and
      (JsPath \ "receiver").read[Handle] and
      (JsPath \ "wasRead").read[Boolean] and
      (JsPath \ "moment").read[Date] and
      (JsPath \ "text").read[String]) (Message.apply _)

  override def save(message: Message): Future[Message] = {
    messages.insert(message) map { _ => message }
  }

  override def getUnread(handle: Handle): Future[Seq[Message]] = {
    messages.find(Json.obj(
        "receiver" -> Json.toJson(handle),
        "wasRead" -> false))
      .cursor[Message]()
      .collect[Seq]()
    }

  override def getLast(sender: Handle, receiver: Handle, upTo: Int = Int.MaxValue): Future[Seq[Message]] = {
    messages.find(Json.obj(
        "receiver" -> Json.toJson(receiver),
        "sender" -> Json.toJson(sender)))
        .sort(Json.obj("moment" -> -1))
        .cursor[Message]()
        .collect[Seq](upTo)
  }

  def messages = mongo.db.collection[JSONCollection]("messages")

  override def markRead(messageSeq: Seq[Message]): Future[Seq[Message]] = {
    messages.update(Json.obj("_id" -> Json.toJson(messageSeq map { message => message.id })),
      Json.obj("$set" -> Json.obj("wasRead" -> Json.toJson(true)))) map { _ => messageSeq }
  }
}

