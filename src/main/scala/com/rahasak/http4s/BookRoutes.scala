package com.rahasak.http4s

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

object BookRoutes {
  def routes(bookRepo: BookRepoImpl): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._

    HttpRoutes.of[IO] {
      case _@GET -> Root / "books" =>
        bookRepo.getBooks().flatMap(b => Ok(b))
      case req@POST -> Root / "books" =>
        req.decode[Book] { b =>
          bookRepo.createBook(b).flatMap(id => Created(Json.fromInt(id)))
        }
      case _@GET -> Root / "books" / id =>
        bookRepo.getBook(id.toInt) flatMap {
          case None => NotFound()
          case Some(book) => Ok(book)
        }
    }
  }
}
