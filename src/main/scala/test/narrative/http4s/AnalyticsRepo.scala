package test.narrative.http4s

import cats.effect.IO


trait AnalyticsRepo {
  def addAnalytics(newData: PostAnalytics): IO[Int]

  def queryAnalytics(timestamp: Long): IO[Option[GetAnalytics]]

}


