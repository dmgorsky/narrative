import sbt.Keys.version

lazy val test_service =
  project
    .in(file("."))
    .settings(
      name := "narrative-test",
      version := "1.0",
      scalaVersion := "2.13.8")
    .settings(
      libraryDependencies ++= {

        lazy val doobieVersion = "1.0.0-RC2"
        lazy val http4sVersion = "0.23.11"
        lazy val circeVersion = "0.14.1"

        Seq(
          "org.tpolecat" %% "doobie-core" % doobieVersion,
          "org.tpolecat" %% "doobie-postgres" % doobieVersion,
          "org.tpolecat" %% "doobie-hikari" % doobieVersion,
          "org.tpolecat" %% "doobie-specs2" % doobieVersion,
          "org.http4s" %% "http4s-ember-server" % http4sVersion,
          //    "org.http4s"            %% "http4s-blaze-server"    % http4sVersion,
          "org.http4s" %% "http4s-circe" % http4sVersion,
          "org.http4s" %% "http4s-dsl" % http4sVersion,
          "io.circe" %% "circe-core" % circeVersion,
          "io.circe" %% "circe-generic" % circeVersion,
          "io.circe" %% "circe-config" % "0.8.0",
          "org.postgresql" % "postgresql" % "42.3.3",
          "org.slf4j" % "slf4j-api" % "1.7.5",
          "ch.qos.logback" % "logback-classic" % "1.2.11"
        )

      })
    .enablePlugins(DockerPlugin)
    .enablePlugins(JavaAppPackaging)
    .settings(
      dockerExposedPorts := Seq(8080),
      dockerBaseImage := "cafapi/java-postgres",
      Docker / version := "latest",
      Docker / packageName := "narrative-test",
    )

resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
)
