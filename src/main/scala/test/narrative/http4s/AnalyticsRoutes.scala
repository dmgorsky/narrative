package test.narrative.http4s

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

object AnalyticsRoutes {
  def routes(analyticsRepo: AnalyticsRepo): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._

    object TimestampQueryParamMatcher extends QueryParamDecoderMatcher[Long]("timestamp")
    object UserQueryParamMatcher extends QueryParamDecoderMatcher[String]("user")
    object EventQueryParamMatcher extends QueryParamDecoderMatcher[String]("event")

    HttpRoutes.of[IO] {
      case _@GET -> Root / "analytics" :? TimestampQueryParamMatcher(timestamp) =>
        analyticsRepo.queryAnalytics(timestamp).flatMap(a => Ok(a))
      case _@POST -> Root / "analytics" :? TimestampQueryParamMatcher(timestamp) +& UserQueryParamMatcher(user_id) +& EventQueryParamMatcher(event) =>
        analyticsRepo.addAnalytics(PostAnalytics(timestamp, user_id, event)).flatMap(_ => NoContent())
    }
  }
}
