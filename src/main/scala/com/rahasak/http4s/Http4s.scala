package com.rahasak.http4s

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import doobie.util.transactor.Transactor
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{Request, Response}

object Http4s extends IOApp {

  def makeRouter(transactor: Transactor[IO]): Kleisli[IO, Request[IO], Response[IO]] = {
    Router[IO](
      "/api/v1" -> AccountRoutes.routes(new AccountRepoImpl(transactor))
    ).orNotFound
  }

  def serveStream(transactor: Transactor[IO], serverConfig: ServerConfig): Stream[IO, ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(serverConfig.port, serverConfig.host)
      .withHttpApp(makeRouter(transactor))
      .serve
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val stream = for {
      config <- Stream.eval(Config.load())
      xa <- Stream.eval(Database.transactor(config.dbConfig))
      _ <- Stream.eval(Database.bootstrap(xa))
      exitCode <- serveStream(xa, config.serverConfig)
    } yield exitCode

    stream.compile.drain.as(ExitCode.Success)
  }

}
