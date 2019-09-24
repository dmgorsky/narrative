package com.rahasak.monix

import monix.eval.Task
import monix.execution.CancelableFuture
import scala.concurrent.duration._

object Monix extends App {

  val task = Task.eval {
    println("lambda")
    "lambda"
  }

  println("hoooo")

  implicit val scheduler = monix.execution.Scheduler.global
  val result: CancelableFuture[String] = task.runAsync
  task.runAsync

  val t = task.delayResult(3.seconds)

}
