package test.narrative.http4s

import cats.effect.{IO, Resource}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

object Database {
  def transactor(dbConfig: DbConfig) = {
    // hikari config
    val config = new HikariConfig()
    config.setJdbcUrl(dbConfig.url)
    config.setUsername(dbConfig.username)
    config.setPassword(dbConfig.password)
    config.setMaximumPoolSize(dbConfig.poolSize)
    config.setAutoCommit(dbConfig.autoCommit)

    val transactor = for {
      ec <- ExecutionContexts.fixedThreadPool[IO](dbConfig.poolSize)
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.postgresql.Driver",
        dbConfig.url,
        dbConfig.username,
        dbConfig.password, // The password
        ec
      )
      //      transactor <- HikariTransactor.newHikariTransactor[IO](dbConfig.url,
      //        dbConfig.url,
      //        dbConfig.username,
      //        dbConfig.password,
      //        ec
      //      )
      transactor <- HikariTransactor.fromHikariConfig[IO](new HikariDataSource(config), ec)
    } yield xa

    transactor

  }


  def bootstrap(xa: Transactor[IO]): IO[Int] = {
    AnalyticsQueries.createTable.run.transact(xa)
  }

  def populateTest(xa: Transactor[IO]): IO[Int] = {
    AnalyticsQueries.populateTest.run.transact(xa)
  }

//  def bootstrap(xa: Resource[IO, Transactor[IO]]): IO[Int] = {
//    xa.use { xa =>
//      AnalyticsQueries.createTable.run.transact(xa)
//    }
//  }

}
