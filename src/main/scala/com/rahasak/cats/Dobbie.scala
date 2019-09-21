package com.rahasak.cats

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.transactor.Transactor

object Dobbie extends App {

  case class Country(name: String, code: String)

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

  // insert query
  def insert(country: Country): doobie.Update0 = {
    sql"insert into countries (name, code) values (${country.name}, ${country.code})".update
  }

  // find country
  def find(name: String): doobie.ConnectionIO[List[Country]] = {
    sql"select * from countries where name = $name".query[Country].to[List]
  }

  // update query
  def update(name: String, code: String): doobie.Update0 = {
    sql"update countries set code = $code where name = $name".update
  }

  // delete query
  def delete(name: String): doobie.Update0 = {
    sql"delete from countries where name=$name".update
  }

  //find().transact(transactor).unsafeRunSync().foreach(println)
  //insert("aka", "a").run.transact(transactor).unsafeRunSync()
  //update("hooooo", "aka").run.transact(transactor).unsafeRunSync()
  //delete("usa").run.transact(transactor).unsafeRunSync()

  // find
  val f = for {
    xa <- DbTransactor
    result <- find("aka").transact(xa)
  } yield result
  f.unsafeRunSync().foreach(println)

  // insert
  val i = for {
    xa <- DbTransactor
    result <- insert(Country("austria", "sw")).run.transact(xa)
  } yield result
  println(i.unsafeRunSync())

  // update
  val u = for {
    xa <- DbTransactor
    result <- update("sweden", "swed").run.transact(xa)
  } yield result
  println(u.unsafeRunSync())

  // delete
  val d = for {
    xa <- DbTransactor
    result <- delete("sweden").run.transact(xa)
  } yield result
  println(d.unsafeRunSync())

}
