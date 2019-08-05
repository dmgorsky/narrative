package com.rahasak.connect.protocol

import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat, _}

case class Status(code: String, desc: String)

object StatusProtocol extends DefaultJsonProtocol {

  implicit object StatusFormat extends RootJsonFormat[Status] {
    def write(obj: Status) = ???

    def read(json: JsValue): Status =
      json.asJsObject.getFields("OUTRESPONSEDATA") match {
        case Seq(obj: JsObject) =>
          obj.getFields("OUTSTATUS", "OUTRESULTDESC") match {
            case Seq(JsString(c), JsString(d)) =>
              Status(c, d)
            case unrecognized => deserializationError(s"json serialization error $unrecognized")
          }
        case unrecognized => deserializationError(s"json serialization error $unrecognized")
      }
  }

}

