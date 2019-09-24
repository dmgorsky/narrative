package com.rahasak.http4s

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari._
import doobie.util.transactor.Transactor

import scala.collection.mutable

object BookRepoImpl {
  def withDoobieTransactor(): IO[BookRepoImpl] = {
    // hikari pooling config with mysql
    val config = new HikariConfig()
    config.setJdbcUrl("jdbc:mysql://localhost:3306/mystiko")
    config.setUsername("root")
    config.setPassword("root")
    config.setMaximumPoolSize(5)

    // transactor with config
    val transactor: HikariTransactor[IO] = HikariTransactor.apply[IO](new HikariDataSource(config))
    IO {
      new BookRepoImpl(transactor)
    }
  }
}

class BookRepoImpl(xa: Transactor[IO]) extends BookRepo {

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

