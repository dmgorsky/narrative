name := "cats"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {

  lazy val doobieVersion = "0.5.3"

  Seq(
    "org.tpolecat"              %% "doobie-core"            % doobieVersion,
    "org.tpolecat"              %% "doobie-postgres"        % doobieVersion,
    "org.tpolecat"              %% "doobie-hikari"          % doobieVersion,
    "org.tpolecat"              %% "doobie-specs2"          % doobieVersion,
    "mysql"                     % "mysql-connector-java"    % "5.1.34",
    "org.slf4j"                 % "slf4j-api"               % "1.7.5",
    "ch.qos.logback"            % "logback-classic"         % "1.0.9",
    "org.scalatest"             % "scalatest_2.11"          % "2.2.1"             % "test"
  )
}

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)
