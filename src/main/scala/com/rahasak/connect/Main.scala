package com.rahasak.connect

import com.rahasak.connect.protocol._
import spray.json.DefaultJsonProtocol._
import spray.json._

object Main extends App {

  handleLanguage()
  handleToken()
  handleResponse()
  handleStatus()
  handleAccount()
  handleTransaction()
  handlePromize()
  handleDocument()

  /**
    * Handle plain object
    */
  def handleLanguage(): Unit = {
    implicit val languageFormat: JsonFormat[Language] = jsonFormat3(Language)
    val obj = Language("001", "haskell", isFunctional = true)
    println(obj.toJson.toString())

    val json =
      """
        |{
        |  "id":"001",
        |  "isFunctional":true,
        |  "name":"haskell"
        |}
      """.stripMargin
    println(json.parseJson.convertTo[Language])
  }

  /**
    * Handle objects with option
    */
  def handleToken(): Unit = {
    implicit val tokenFormat: JsonFormat[Token] = jsonFormat4(Token)
    val obj = Token("001", "rahasak", "labs", None)
    println(obj.toJson.toString)

    val json =
      """
        |{
        |  "id":"001",
        |  "name":"rahasak",
        |  "value":"labs"
        |}
      """.stripMargin
    println(json.parseJson.convertTo[Token])
  }

  /**
    * Handle nested object
    */
  def handleResponse(): Unit = {
    implicit val metaFormat: JsonFormat[Meta] = jsonFormat4(Meta)
    implicit val userFormat: JsonFormat[User] = jsonFormat3(User)
    implicit val responseFormat: JsonFormat[Response] = jsonFormat2(Response)
    val obj = Response(
      Meta(0, 10, 3, 243),
      List(
        User("001", "lambda", 26),
        User("002", "ops", 28),
        User("003", "rahasak", 26)
      )
    )
    println(obj.toJson.toString())

    val json =
      """
        |{
        |  "meta":{"count":3,"limit":10,"offset":0,"total":243},
        |  "users":[
        |    {"age":26,"id":"001","name":"lambda"},
        |    {"age":28,"id":"002","name":"ops"},
        |    {"age":26,"id":"003","name":"rahasak"}
        |  ]
        |}
      """.stripMargin
    println(json.parseJson.convertTo[Response])
  }

  /**
    * Custom read
    */
  def handleStatus(): Unit = {
    import StatusProtocol._
    val json =
      """
        {
          "OUTRESPONSEDATA":{
            "OUTSTATUS":"1",
            "OUTRESULTDESC": "ACCOUNT-POSTING COMPLETED"
          }
        }
      """.stripMargin
    println(json.parseJson.convertTo[Status])
  }

  /**
    * Complex read
    */
  def handleAccount(): Unit = {
    import AccountProtocol._
    val json =
      """
        |{"OUTCIF_NIC":"87231212V",
        |"OUTACCOUNTNOS":["367431112","15764288","","","","","","","","","","","","","","","","",""],
        |"OUTRELATIONSHIP":["SOW","SOW","","","","","","","","","","","","","","","","","","","",""],
        |"OUTSHORTNAME":["RAHASAK LABS","RAHASAK LABS","","","","","","","","","","","","","","","","","",""],
        |"OUTACCOUNTTYPE":["Savings","Savings","","","","","","","","","","","","","","","","","","","",""],
        |"OUTACCOUNTBALANCE":["00000000000000000","00000000008000000","","","","","","","","","","","","","",""]}
      """.stripMargin
    println(json.parseJson.convertTo[Account])
  }

  /**
    * Custom write
    */
  def handleTransaction(): Unit = {
    import TransactionProtocol._
    val obj = Transaction("1112233", "445566", "3500", null, "rahasak transfer")
    println(obj.toJson.toString())
  }

  /**
    * Handle with trait type
    */
  def handlePromize(): Unit = {
    import PromizeProtocol._
    val obj = Create("create", "rahasak", "001", "lambda", "ops", "12100", "dep")
    println(obj.toJson.toString)

    val json =
      """
        |{"messageType":"approve","execer":"rahasak","id":"001","salt":"83121"}
      """.stripMargin
    println(json.parseJson.convertTo[Promize])
  }

  /**
    * Handle objects more than 22 fields
    */
  def handleDocument(): Unit = {
    import DocumentProtocol._
    val obj = Document(
      "field1", "field2", "field3", "field4", "field5", "field6",
      "field7", "field8", "field9", "field10", "field11", "field12",
      "field13", "field14", "field15", "field16", "field17", "field18",
      "field19", "field20", "field21", "field22", "field23", "field24",
      approved = true
    )
    println(obj.toJson.toString())

    val json =
      """
        |{
        |  "field1":"field1",
        |  "field2":"field2",
        |  "field3":"field3",
        |  "field4":"field4",
        |  "field5":"field5",
        |  "field6":"field6",
        |  "field7":"field7",
        |  "field8":"field8",
        |  "field9":"field9",
        |  "field10":"field10",
        |  "field11":"field11",
        |  "field12":"field12",
        |  "field13":"field13",
        |  "field14":"field14",
        |  "field15":"field15",
        |  "field16":"field16",
        |  "field17":"field17",
        |  "field18":"field18",
        |  "field19":"field19",
        |  "field20":"field20",
        |  "field21":"field21",
        |  "field22":"field22",
        |  "field23":"field23",
        |  "field24":"field24",
        |  "approved":true
        |}
      """.stripMargin
    println(json.parseJson.convertTo[Document])
  }

}
