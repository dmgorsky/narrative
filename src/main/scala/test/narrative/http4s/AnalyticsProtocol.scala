package test.narrative.http4s

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}


/**
  * Adding raw data to db
  *
  * @param millis_since_epoch an hour to get analytics for
  * @param user               user id (Long)
  * @param event              click|impression
  */
final case class PostAnalytics(millis_since_epoch: Long, user: String,
                               event: String /*Refined MatchesRegex[W.`"(click|impression)"`.T]*/)

object PostAnalytics {
  implicit val decoder: Decoder[PostAnalytics] = deriveDecoder[PostAnalytics]
  implicit val encoder: Encoder[PostAnalytics] = deriveEncoder[PostAnalytics]

}

final case class GetAnalytics(unique_users: Int, clicks: Long, impressions: Long)

object GetAnalytics {
  implicit val decoder: Decoder[GetAnalytics] = deriveDecoder[GetAnalytics]
  implicit val encoder: Encoder[GetAnalytics] = deriveEncoder[GetAnalytics]

}
