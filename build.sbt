name := "cats"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {

  lazy val doobieVersion = "0.5.3"
  lazy val Http4sVersion = "0.20.8"
  lazy val CirceVersion = "0.9.1"

  Seq(
    "org.tpolecat"      %% "doobie-core"            % doobieVersion,
    "org.tpolecat"      %% "doobie-postgres"        % doobieVersion,
    "org.tpolecat"      %% "doobie-h2"              % doobieVersion,
    "org.tpolecat"      %% "doobie-hikari"          % doobieVersion,
    "org.tpolecat"      %% "doobie-specs2"          % doobieVersion,
    "org.tpolecat"      %% "doobie-scalatest"       % doobieVersion       % "test",
    "io.monix"          %% "monix"                  % "2.3.3",
    "org.http4s"        %% "http4s-blaze-server"    % Http4sVersion,
    "org.http4s"        %% "http4s-blaze-client"    % Http4sVersion,
    "org.http4s"        %% "http4s-circe"           % Http4sVersion,
    "org.http4s"        %% "http4s-dsl"             % Http4sVersion,
    "io.circe"          %% "circe-generic"          % CirceVersion,
    "mysql"             % "mysql-connector-java"    % "5.1.34",
    "org.slf4j"         % "slf4j-api"               % "1.7.5",
    "ch.qos.logback"    % "logback-classic"         % "1.0.9"
  )

}

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)
