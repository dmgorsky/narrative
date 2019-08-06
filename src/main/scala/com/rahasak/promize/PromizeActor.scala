package com.rahasak.promize

import akka.actor.{Actor, Props}
import com.rahasak.promize.PromizeActor.{Request, Response}

object PromizeActor {

  case class Request(uri: String)

  case class Response(payload: String)

  def props() = Props(new PromizeActor)

}

class PromizeActor extends Actor {

  override def receive: Receive = {
    case Request(uri) =>
      Thread.sleep(3000)

      if (!uri.startsWith("http")) throw new IllegalArgumentException("invalid uri")
      sender ! Response("cmFoYXNhayBsYWJz")
  }

}


