package com.rahasak.cats

import cats.Functor
import cats.data.OptionT

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import cats.implicits._

object Main extends App {

  val f = Future.successful(Option(10))

  implicit val context = ExecutionContext.Implicits.global

  def ops() = {
    val t = f.map(_.map(_ + 10))
    val r = Await.result(t, 10.seconds)
    println(r)
  }

  def opsT() = {
    val fT = OptionT(f)
    val t = fT.filter(_ > 8)
    val r = t.getOrElse(10)
    println(Await.result(t.value, 10.seconds))
  }

  def func() = {
    val len: String => Int = _.length
    val l = Functor[List].map(List("scala", "erlang", "hack"))(len)
    println(l)
  }

  def either() = {
    import cats.syntax.functor._
    val e: Either[String, Int] = Right(1000)
    e.map(x => x)
  }

  ops()
  opsT()
  func()
}
