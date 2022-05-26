package test.narrative.http4s

import cats.data.Kleisli
import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import com.comcast.ip4s.{Host, Port}
import doobie.util.transactor.Transactor
import fs2.Stream
import io.circe.config.parser
import org.http4s.implicits._
import org.http4s.server.{Router, Server}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.{Request, Response}
import org.slf4j.LoggerFactory

object Main extends IOApp {

  val log = LoggerFactory.getLogger(Main.getClass())

  def makeRouter(transactor: Transactor[IO]): Kleisli[IO, Request[IO], Response[IO]] = {
    Router[IO](
      "/" -> AnalyticsRoutes.routes(new AnalyticsRepoImpl(transactor))
    ).orNotFound
  }

  def serveHttp(transactor: Transactor[IO], serverConfig: ServerConfig) = {
    EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString(serverConfig.host).get)
      .withPort(Port.fromInt(serverConfig.port).get)
      .withHttpApp(makeRouter(transactor))
      .build
  }

  override def run(args: List[String]): IO[ExitCode] = {
    import cats.effect.IO
    import io.circe.config.parser
    import io.circe.generic.auto._

    val program = for {
      dbConf <- parser.decodePathF[IO, DbConfig]("db")
      serverConf <- parser.decodePathF[IO, ServerConfig]("server")
      xa = Database.transactor(dbConf)
      resources = for {
        transactor <- xa
        http <- serveHttp(transactor, serverConf)
      } yield (transactor, http)

      fiber = resources.use { case (xa, server) =>
        Database.bootstrap(xa).unsafeRunSync()
        Database.populateTest(xa).unsafeRunSync()
        log.info("Database initialized")
        IO.delay(log.info("Server started at {}", server.address)) >> IO.never.as(ExitCode.Success)
      }
    } yield fiber

    program.attempt.unsafeRunSync() match {
      case Left(e) =>
        IO {
          log.error("An error occurred during execution!", e)
          ExitCode.Error
        }
      case Right(s) => s
    }


  }

}
