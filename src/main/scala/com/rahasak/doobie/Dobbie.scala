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
  val i1 = for {
    xa <- transactor
    result <- Query.insert(Document("002", "rahasak", System.currentTimeMillis() / 100)).run.transact(xa)
  } yield result
  println(i1.unsafeRunSync())

  // insert
  val i2 = for {
    xa <- transactor
    result <- Query.insert(Document("001", "rahasak", System.currentTimeMillis() / 100)).run.transact(xa)
  } yield result
  println(i2.unsafeRunSync())

  // search
  val s1 = for {
    xa <- transactor
    result <- Query.search("rahasak").to[List].transact(xa)
  } yield result
  s1.unsafeRunSync().foreach(println)

  // search with id
  val s2 = for {
    xa <- transactor
    result <- Query.searchWithId("001").option.transact(xa)
  } yield result
  println(s2.unsafeRunSync())

  // search with fragment
  val s3 = for {
    xa <- transactor
    result <- Query.searchWithFragment("rahasak", asc = true).to[List].transact(xa)
  } yield result
  s3.unsafeRunSync().foreach(println)

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
