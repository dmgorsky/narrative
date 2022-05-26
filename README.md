# test_sample_1 #

assignment work created for the following test:

## The task ##

As a part of integrating with our partners, Narrative supports collecting data on website visitors and returning some
basic analytics on those visitors. The goal of this task is to implement a basic endpoint for this use case. It should
accept the following over HTTP:

```scala
POST / analytics ? timestamp = {
  millis_since_epoch
} & user = {
  user_id
} & event = {
  click | impression
}
GET / analytics ? timestamp = {
  millis_since_epoch
}
```

When the POST request is made, a 204 is returned to the client with an empty response. We simply side-effect and track
the event in our data store. When the GET request is made, we return information in the following format to the client,
for the hour (assuming GMT time zone) of the requested timestamp:

```scala
unique_users
,
{
  number_of_unique_usernames
}
clicks
,
{
  number_of_clicks
}
impressions
,
{
  number_of_impressions
}
```

It is worth noting that the traffic pattern is typical of time series data. The service will receive many more GET
requests (~95%) for the current hour than for past hours (~5%). The same applies for POST requests.

## System requirements ##

- docker

## Steps ##

I've created a project from the some template with http4s.

For sure, there was a temptation to play with zio over cats, but I'm sure that microservice architecture it really doesn't matter which languages the services are implemented with, so let this be in typelevel stack; (further could be utilizing ZIO or Clojure/Rust/Python/Go. For instance, if the cluster uses grpc).

For sake of triviality also didn't expand the tapir/swagger section as well (it helped me earlier though, when integrating with another microservices).

Added an evolution to flyway:
```postgresql
CREATE TABLE "user_interactions" (
    "user_id"   VARCHAR                  NOT NULL,
    "event"     VARCHAR(10)              NOT NULL
);
```

Added endpoints:

```scala
    HttpRoutes.of[IO] {
  case _@GET -> Root / "analytics" :? TimestampQueryParamMatcher(timestamp) =>
    analyticsRepo.queryAnalytics(timestamp).flatMap(a => Ok(a))
  case _@POST -> Root / "analytics" :? TimestampQueryParamMatcher(timestamp) +& UserQueryParamMatcher(user_id) +& EventQueryParamMatcher(event) =>
    analyticsRepo.addAnalytics(PostAnalytics(timestamp, user_id, event)).flatMap(_ => NoContent())
}
```

The project already uses Postgresql driver, so first I've added logics (AnalyticsRepoImpl.scala):

```scala
override def addAnalytics(newData: PostAnalytics) = {
  println(s"adding $newData")
  AnalyticsQueries.insert(newData).run.transact(xa)
}

override def queryAnalytics(timestamp: Long) = {
  println(s"getting for $timestamp")
  AnalyticsQueries.query(timestamp).transact(xa)
}

```
doobie's transactor using a connection pool with an own thread pool (usually is tuned accordingly, but didn't clarify further to avoid overkills in a test assignment, just informational).

<br/>

pretty straightforward insert (even w/o upsert checks):
```scala
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
```

and select using common table expression ('with')

```scala
  def query(timestamp: Long) = {
  val statement =
    sql"""
        WITH int_window AS (
            SELECT user_id, event
            FROM user_interactions
            WHERE timestamp::DATE = to_timestamp($timestamp)::DATE AND
                pg_catalog.date_part('hour', timestamp::DATE) = pg_catalog.date_part('hour', to_timestamp($timestamp)::DATE)
         )

        SELECT count(DISTINCT int_window.user_id) AS unique_uqers
          ,count(*) FILTER(WHERE event = 'click') AS clicks
          ,count(*) FILTER(WHERE event = 'impression') as impressions
        FROM int_window
        """
  statement.query[GetAnalytics].option
}
```



Now, the {I hope not preliminary optimization} recommendation to prefer the last hour data.

We can:
* use (_auto updated_) materialized view
* just hope that columnar databases do aggregates better
* add a computed column (or a trigger) with a date+hour hash to filter faster
* schedule a procedure that archives the data out of the hour
* buffer last hour data in memory, swapping by chance
*

I chose the 'timescaledb' postgresql extension that splits data chunks under umbrella hypertable into sub-tables based on an hour in timestamp:
```postgresql

SELECT create_hypertable('user_interactions', 'timestamp');

SELECT set_chunk_time_interval('user_interactions', INTERVAL '1 hour');

```

Let's prove that.
Insert series of user interactions for 6 months:
```postgresql
insert into user_interactions
SELECT time::timestamptz as timestamp,
       (abs(random())*30)::int as user_id,
       ('{click,impression}'::text[])[ceil(random()*2)] as event
FROM generate_series(TIMESTAMP '2020-01-01 00:00:00',
                     TIMESTAMP '2020-06-01 00:00:00',
                     INTERVAL '1 min') AS time;
```
This gives us 152 days of data, so we expect `152 * 24 = 3'648` chunks.
Call the `show_chunks` function:
```postgresql
select date_part('days', (timestamp '2020-06-01' - timestamp '2020-01-01') * 24) as est_hours
, count(*) from show_chunks('user_interactions') as chunks
```

| est_hours | count |
|-----------|-------|
| 3648      | 3649  |

Making no changes to code, our filters/aggregates should work with per hour data at the same speed.

Also, to start the service, I've prepared the container built from alpine-based timescale/timescaledb:latest-pg14
```docker
#FROM postgres:14-alpine
FROM timescale/timescaledb:latest-pg14
EXPOSE 8080
ENV POSTGRES_USER=narrative
ENV POSTGRES_PASSWORD=narrative
ENV POSTGRES_DB=narrative
ENV PGDATA=/var/lib/postgresql/data
RUN apk add openjdk17-jre
WORKDIR /opt/docker
ADD target/docker/stage/1/opt/docker /opt/docker
ADD target/docker/stage/2/opt/docker /opt/docker
ADD docker/start_svc.sh /opt/docker
RUN chmod 777 /opt/docker/start_svc.sh
RUN chmod 777 /opt/docker/bin/narrative-test
ENTRYPOINT ["/opt/docker/start_svc.sh"]
CMD ["postgres"]

```

It uses code and libs prepared by `sbt docker:stage` (I definitely will build a native graalvm image with all that dependency tree shaking, but next time, so far let docker:stage prepare jars for lazy me), as well as modified script from `/usr/local/bin/docker-entrypoint.sh` - to start postgres and the narrative-test service (multi-stage docker build).

So the script to start is `run_narrative_test.sh`:
```shell
sbt docker:stage
docker build -t narrative-test -f docker/Dockerfile .
docker run -it -p 18083:8080 -p 5432:5432 --privileged --rm narrative-test
```
(http port is `18083`)

This is just a demo of a starting service, so the next step is testing.
Usually having tests for database logics or http4s endpoints is useful, for instance, for monitoring changing interfaces, but not in this assignment.
Also, I tend to do integration testing using k8s namespaces (jx, devspace, tilt, skaffold, draft, garden), but this is not a pure development, rather SRE.

`<nerd mode on>` _I still think having integration tests with so-called preview-environments (on-demand clusters inside our cluster) running needed versions of communicating services for the test is less prone to mocking errors_ `</nerd mode off>`

Let's work on some data loaded for hour chunks.
```shell
ab -n 1000 -c 50 -m POST "http://localhost:18083/analytics?timestamp=10032342000&user=user2&event=click"
ab -n 1000 -c 50 "http://localhost:18083/analytics?timestamp=10032342000"
```

