package test.narrative.http4s

import doobie.implicits._
import doobie._

import java.sql.Timestamp
import java.time.Instant
import doobie.implicits.toSqlInterpolator
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.implicits._
import doobie.implicits.javasql._

object AnalyticsQueries {

  def createDb = {
    sql"""
         |CREATE DATABASE narrative
       """
      .update
  }

  def createTable = {
    sql"""
         |CREATE TABLE IF NOT EXISTS user_interactions (
         |  "timestamp" TIMESTAMP WITH TIME ZONE NOT NULL,
         |  "user_id"   VARCHAR                  NOT NULL,
         |  "event"     VARCHAR(10)              NOT NULL
         |)
       """.stripMargin
      .update
  }
  def populateTest = {
    sql"""
         |insert into user_interactions
         |   SELECT time::timestamptz as timestamp,
         |     (abs(random())*30)::int as user_id,
         |     ('{click,impression}'::text[])[ceil(random()*2)] as event
         |   FROM generate_series(TIMESTAMP '2020-01-01 00:00:00',
         |      TIMESTAMP '2020-06-01 00:00:00',
         |      INTERVAL '1 min') AS time;
       """
      .stripMargin
      .update
  }

  // insert query
  def insert(newData: PostAnalytics) = {
    val ts = Timestamp.from(Instant.ofEpochMilli(newData.millis_since_epoch))
    sql"""
         |INSERT INTO user_interactions (
         |  timestamp,
         |  user_id,
         |  event
         |)
         |VALUES (
         |  ${ts},
         |  ${newData.user},
         |  ${newData.event}
         |)
        """.stripMargin
      .update
  }

  def query(timestamp: Long) = {
    val tsInSeconds = timestamp / 1000
    val statement =
      sql"""
        WITH int_window AS (
            SELECT user_id, event
            FROM user_interactions
            WHERE timestamp::DATE = to_timestamp($tsInSeconds)::DATE AND
                pg_catalog.date_part('hour', timestamp::DATE) = pg_catalog.date_part('hour', to_timestamp($tsInSeconds)::DATE)
         )

        SELECT count(DISTINCT int_window.user_id) AS unique_uqers
          ,count(*) FILTER(WHERE event = 'click') AS clicks
          ,count(*) FILTER(WHERE event = 'impression') as impressions
        FROM int_window
        """
    statement.query[GetAnalytics].option
  }


}
