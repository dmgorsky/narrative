package test.narrative.http4s

import cats.effect.{IO, Resource}
import doobie.implicits._
import doobie.util.transactor.Transactor

class AnalyticsRepoImpl(xa: Transactor[IO]) extends AnalyticsRepo {

  override def addAnalytics(newData: PostAnalytics) = {
    println(s"adding $newData")
    AnalyticsQueries.insert(newData).run.transact(xa)
  }

  override def queryAnalytics(timestamp: Long) = {
    println(s"getting for $timestamp")
    AnalyticsQueries.query(timestamp).transact(xa)
  }

}


