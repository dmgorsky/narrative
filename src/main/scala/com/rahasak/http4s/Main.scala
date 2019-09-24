package com.rahasak.http4s

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import doobie.util.transactor.Transactor
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {

  def makeRouter(transactor: Transactor[IO]) = {
    Router[IO](
      "/api/v1" -> BookRoutes.routes(new BookRepoImpl(transactor))
    ).orNotFound
  }

  def serveStream(transactor: Transactor[IO]) = {
    BlazeServerBuilder[IO]
      .bindHttp(8761, "0.0.0.0")
      .withHttpApp(makeRouter(transactor))
      .serve
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val stream = for {
      config <- Stream.eval(Config.load())
      xa <- Stream.eval(Database.transactor(config))
      _ <- Stream.eval(Database.init(xa))
      exitCode <- serveStream(xa)
    } yield exitCode

    stream.compile.drain.as(ExitCode.Success)
  }

}
