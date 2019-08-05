package com.rahasak.connect.protocol

import spray.json._

trait Promize

case class Create(messageType: String, execer: String, id: String, from: String, to: String, amount: String, typ: String) extends Promize

case class Approve(messageType: String, execer: String, id: String, salt: String) extends Promize

object PromizeProtocol extends DefaultJsonProtocol {
  implicit val createFormat: JsonFormat[Create] = jsonFormat7(Create)
  implicit val approveFormat: JsonFormat[Approve] = jsonFormat4(Approve)

  implicit object PromizeMessageFormat extends RootJsonFormat[Promize] {
    def write(obj: Promize): JsValue =
      JsObject((obj match {
        case c: Create => c.toJson
        case a: Approve => a.toJson
        case unknown => deserializationError(s"json deserialize error: $unknown")
      }).asJsObject.fields)

    def read(json: JsValue): Promize =
      json.asJsObject.getFields("messageType") match {
        case Seq(JsString("create")) => json.convertTo[Create]
        case Seq(JsString("approve")) => json.convertTo[Approve]
        case unrecognized => serializationError(s"json serialization error $unrecognized")
      }
  }

}
