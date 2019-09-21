package com.rahasak.doobie

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.implicits._

object Dobbie extends App {

  // hikari pooling config with mysql
  val config = new HikariConfig()
  config.setJdbcUrl("jdbc:mysql://localhost:3306/mystiko")
  config.setUsername("root")
  config.setPassword("root")
  config.setMaximumPoolSize(5)

  // transactor with config
  val transactor: IO[HikariTransactor[IO]] =
    IO.pure(HikariTransactor.apply[IO](new HikariDataSource(config)))

  // create table
  val c = for {
    xa <- transactor
    result <- Query.createTable.run.transact(xa)
  } yield result
  println(c.unsafeRunSync())

  // insert
  val i = for {
    xa <- transactor
    result <- Query.insert(Document("002", "rahasak", System.currentTimeMillis() / 100)).run.transact(xa)
  } yield result
  println(i.unsafeRunSync())

  // search
  val s = for {
    xa <- transactor
    result <- Query.search("001").to[List].transact(xa)
  } yield result
  s.unsafeRunSync().foreach(println)

  // search with fragment
  val f = for {
    xa <- transactor
    result <- Query.searchWithFragment("rahasak", asc = true).to[List].transact(xa)
  } yield result
  f.unsafeRunSync().foreach(println)

  // update
  val u = for {
    xa <- transactor
    result <- Query.update("001", "rahasak-labs").run.transact(xa)
  } yield result
  println(u.unsafeRunSync())

  // delete
  val d = for {
    xa <- transactor
    result <- Query.delete("001").run.transact(xa)
  } yield result
  println(d.unsafeRunSync())

}
