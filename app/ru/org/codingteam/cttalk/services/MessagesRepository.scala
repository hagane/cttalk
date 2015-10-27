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
import ru.org.codingteam.cttalk.models.{Message, Token}

import scala.concurrent.Future

/**
 * Created by hgn on 25.10.2015.
 */
@ImplementedBy(classOf[MessagesRepositoryImpl])
trait MessagesRepository {
  def save(message: Message): Future[String]

  def getUnreadFor(token: Token): Future[Seq[Message]]

  def getLastFor(token: Token, upTo: Int = Int.MaxValue): Future[Seq[Message]]

  def markRead(messageSeq: Seq[Message]): Future[Seq[Message]]
}

class MessagesRepositoryImpl @Inject()(mongo: ReactiveMongoApi, tokens: TokensRepository) extends MessagesRepository {

  implicit val writes = ((JsPath \ "_id").write[String] and
      (JsPath \ "sender").write[String] and
      (JsPath \ "receiver").write[String] and
      (JsPath \ "wasRead").write[Boolean] and
      (JsPath \ "moment").write[Date] and
      (JsPath \ "text").write[String]) {m: Message => (m.id, m.senderToken, m.receiverToken, m.wasRead, m.moment, m.text)}

  implicit val reads = ((JsPath \ "sender").read[String] and
      (JsPath \ "receiver").read[String] and
      (JsPath \ "wasRead").read[Boolean] and
      (JsPath \ "moment").read[Date] and
      (JsPath \ "text").read[String]) (Message.apply _)

  override def save(message: Message): Future[String] = {
    messages.insert(message) map { _ => message.id }
  }

  override def getUnreadFor(token: Token): Future[Seq[Message]] = {
    tokens.getAllRelated(token) flatMap { related =>
      messages.find(Json.obj("receiver" -> Json.obj("$in" -> Json.toJson(related map {t => t._id})), "wasRead" -> false))
      .cursor[Message]()
      .collect[Seq]()
    }
  }

  override def getLastFor(token: Token, upTo: Int = Int.MaxValue): Future[Seq[Message]] = {
    tokens.getAllRelated(token) flatMap { related =>
      messages.find(Json.obj("receiver" -> Json.obj("$in" -> Json.toJson(related map {t => t._id}))))
        .sort(Json.obj("moment" -> -1))
        .cursor[Message]()
        .collect[Seq](upTo)
    }
  }

  def messages = mongo.db.collection[JSONCollection]("messages")

  override def markRead(messageSeq: Seq[Message]): Future[Seq[Message]] = {
    messages.update(Json.obj("_id" -> Json.toJson(messageSeq map { message => message.id })),
      Json.obj("$set" -> Json.obj("wasRead" -> Json.toJson(true)))) map { _ => messageSeq }
  }
}

