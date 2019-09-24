package com.rahasak.http4s

import cats.effect.IO
import com.rahasak.doobie.Document
import io.circe.Json
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

object DocumentRoutes {
  def routes(documentRepo: DocumentRepo): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._

    HttpRoutes.of[IO] {
      case req@POST -> Root / "documents" =>
        req.decode[Document] { d =>
          documentRepo.createDocument(d).flatMap(id => Created(Json.fromInt(id)))
        }
      case req@PUT -> Root / "documents" / id =>
        req.decode[Document] { d =>
          documentRepo.updateDocument(id, d).flatMap(_ => Accepted())
        }
      case _@GET -> Root / "documents" =>
        documentRepo.getDocuments().flatMap(_ => Ok())
      case _@GET -> Root / "documents" / id =>
        documentRepo.getDocument(id) flatMap {
          case None => NotFound()
          case Some(book) => Ok(book)
        }
    }
  }
}
