package com.rahasak.cats

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.transactor.Transactor

object Dobbie extends App {

  case class Document(id: String, name: String, timestamp: Long)

  // hikari pooling config
  val config = new HikariConfig()
  config.setJdbcUrl("jdbc:mysql://localhost:3306/ops")
  config.setUsername("root")
  config.setPassword("root")
  config.setMaximumPoolSize(5)

  // transactor with config
  val DbTransactor: IO[HikariTransactor[IO]] =
    IO.pure(HikariTransactor.apply[IO](new HikariDataSource(config)))

  val transactor = Transactor.fromDriverManager[IO](
    "com.mysql.jdbc.Driver",
    "jdbc:mysql://localhost:3306/ops",
    "root",
    "root"
  )

  //find().transact(transactor).unsafeRunSync().foreach(println)
  //insert("aka", "a").run.transact(transactor).unsafeRunSync()
  //update("hooooo", "aka").run.transact(transactor).unsafeRunSync()
  //delete("usa").run.transact(transactor).unsafeRunSync()

  // find
  val f = for {
    xa <- DbTransactor
    result <- Queries.find("aka").to[List].transact(xa)
  } yield result
  f.unsafeRunSync().foreach(println)

  // fragment find
  val frag = for {
    xa <- DbTransactor
    result <- Queries.fragmentFind("labs", asc = true).to[List].transact(xa)
  } yield result
  frag.unsafeRunSync().foreach(println)

  // insert
  val i = for {
    xa <- DbTransactor
    result <- Queries.insert(Document("001", "rahasak", System.currentTimeMillis() / 100)).run.transact(xa)
  } yield result
  println(i.unsafeRunSync())

  // update
  val u = for {
    xa <- DbTransactor
    result <- Queries.update("001", "labs").run.transact(xa)
  } yield result
  println(u.unsafeRunSync())

  // delete
  val d = for {
    xa <- DbTransactor
    result <- Queries.delete("001").run.transact(xa)
  } yield result
  println(d.unsafeRunSync())

}
