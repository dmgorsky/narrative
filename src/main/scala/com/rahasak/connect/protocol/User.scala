package com.rahasak.connect.protocol

import spray.json.{DefaultJsonProtocol, JsObject, JsValue, RootJsonFormat, _}

case class Meta(offset: Int, limit: Int, count: Int, total: Int)

case class User(id: String, name: String, age: Int)

case class Response(meta: Meta, users: List[User])

object ResponseProtocol extends DefaultJsonProtocol {

  implicit val metaFormat: JsonFormat[Meta] = jsonFormat4(Meta)
  implicit val userFormat: JsonFormat[User] = jsonFormat3(User)

  implicit object DocumentSearchReplyFormat extends RootJsonFormat[Response] {
    override def write(obj: Response): JsValue = {
      JsObject(
        ("meta", obj.meta.toJson),
        ("documents", obj.`users`.toJson)
      )
    }

    override def read(json: JsValue) = ???
  }

}



