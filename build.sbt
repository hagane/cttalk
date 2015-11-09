import sbt.Project.projectToRef
name := "ct-talk"

version := "0.0-SNAPSHOT"

lazy val root = baseDirectory

lazy val server = (project in file("server")).settings(
  scalaVersion := "2.11.7",
  scalaJSProjects := Seq(client),
  pipelineStages := Seq(scalaJSProd),
  libraryDependencies ++= Seq(
    "org.reactivemongo" %% "play2-reactivemongo" % "0.11.7.play24",
    specs2 % Test
  )
).enablePlugins(PlayScala)
  .aggregate(projectToRef(client))

lazy val client = (project in file("client")).settings(
  scalaVersion := "2.11.7",
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    "com.greencatsoft" %%% "scalajs-angular" % "0.6",
    "com.github.benhutchison" %%% "prickle" % "1.1.9"
  ),
  jsDependencies += "org.webjars" % "angularjs" % "1.3.14" / "angular.js",
  skip in packageJSDependencies := false
).enablePlugins(ScalaJSPlugin, ScalaJSPlay)

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value

routesGenerator := InjectedRoutesGenerator