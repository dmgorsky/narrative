package com.rahasak.http4s

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor

object Database {
  def transactor(config: HikariConfig): IO[HikariTransactor[IO]] = {
    IO {
      // transactor with config
      val transactor: HikariTransactor[IO] = HikariTransactor.apply[IO](new HikariDataSource(config))
      transactor
    }
  }

  def init(xa: Transactor[IO]) = {
    // todo create database
    // todo create tables
    IO {

    }
  }
}
