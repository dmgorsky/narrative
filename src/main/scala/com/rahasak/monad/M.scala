package com.rahasak.monad

object M extends App {

 // val f: Int => (String, String) = (i: Int) => (s"e$i", s"e${i * 3}")
  val f = (i: Int) => List(i - 1, i, i + 1)

  val l = List(2, 4, 1)
  println(l.flatMap(f))
}
