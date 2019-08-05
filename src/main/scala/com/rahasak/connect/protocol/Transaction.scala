package com.rahasak.connect.protocol

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, RootJsonFormat}

case class Transaction(fromAcc: String, toAcc: String, amount: String, reference: String, desc: String,
                       timestamp: Date = new Date())

object TransactionProtocol extends DefaultJsonProtocol {

  def formatDate(date: Date) = {
    val TIME_ZONE = TimeZone.getTimeZone("UTC")
    val TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
    val sdf = new SimpleDateFormat(TIMESTAMP_FORMAT)
    sdf.setTimeZone(TIME_ZONE)

    sdf.format(date)
  }

  implicit object TransactionFormat extends RootJsonFormat[Transaction] {
    def write(obj: Transaction) = JsObject(
      "INENVNAME" -> JsString("PROD"), // add condition based on env variable here
      "INFROMACCOUNT" -> JsString(obj.fromAcc),
      "INTOACCOUNT" -> JsString(obj.toAcc),
      "INAMOUNT" -> JsString(obj.amount),
      "INREFERANCE" -> (if (obj.reference == null) JsString("") else JsString(obj.reference)), // handle null
      "INDESCRIPTION" -> JsString(obj.desc),
      "TIMESTAMP" -> JsString(formatDate(obj.timestamp))
    )

    def read(json: JsValue): Transaction = ???
  }

}
