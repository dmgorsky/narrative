package com.rahasak.http4s

import cats.effect.IO


trait BookRepo {
  def createBook(book: Book): IO[Int]

  def getBook(id: Int): IO[Option[Book]]

  def getBooks(): IO[List[Book]]
}

