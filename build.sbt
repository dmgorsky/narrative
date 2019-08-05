name := "promize"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {

  Seq(
    "com.typesafe.akka"         %% "akka-actor"       % "2.4.14",
    "com.typesafe.akka"         %% "akka-slf4j"       % "2.4.14",
    "io.spray"                  %% "spray-json"       % "1.3.5",
    "org.slf4j"                 % "slf4j-api"         % "1.7.5",
    "ch.qos.logback"            % "logback-classic"   % "1.0.9",
    "org.scalatest"             % "scalatest_2.11"    % "2.2.1"               % "test"
  )
}

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)
