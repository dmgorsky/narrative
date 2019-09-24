package com.rahasak.http4s

import cats.effect.IO
import cats.implicits._
import scala.collection.mutable

class BookRepoImpl extends BookRepo {

  val storage = mutable.HashMap[Int, Book]().empty

  override def createBook(book: Book): IO[Int] = IO {
    storage.put(book.id, book)
    book.id
  }

  override def getBook(id: Int): IO[Option[Book]] = IO {
    storage.get(id)
  }

  override def getBooks(): IO[List[Book]] = IO {
    storage.values.toList
  }
}

