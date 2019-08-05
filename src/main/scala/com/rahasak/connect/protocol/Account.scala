package com.rahasak.connect.protocol

import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat, _}

case class Account(nic: String, nos: List[String], name: String)

object AccountProtocol extends DefaultJsonProtocol {

  implicit object AccountFormat extends RootJsonFormat[Account] {
    def write(obj: Account) = ???

    def read(json: JsValue): Account =
      json.asJsObject.getFields("OUTCIF_NIC", "OUTACCOUNTNOS", "OUTSHORTNAME") match {
        case Seq(JsString(nic), JsArray(JsString(no1) +: JsString(no2) +: _), JsArray(JsString(name) +: _)) =>
          Account(nic, List(no1, no2), name)
        case unrecognized => deserializationError(s"json serialization error $unrecognized")
      }
  }

}
