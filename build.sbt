name := "ct-talk"

scalaVersion := "2.11.7"

version := "0.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.7.play24"
)

libraryDependencies += specs2 % Test

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator