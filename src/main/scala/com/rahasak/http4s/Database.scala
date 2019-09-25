package com.rahasak.http4s

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.transactor.Transactor

object Database {
  def transactor(config: HikariConfig): IO[HikariTransactor[IO]] = {
    // transactor with config
    val transactor: IO[HikariTransactor[IO]] =
      IO.pure(HikariTransactor.apply[IO](new HikariDataSource(config)))
    transactor
  }

  def init(xa: Transactor[IO]): IO[Int] = {
    Query.createTable.run.transact(xa)
  }
}
