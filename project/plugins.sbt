scalaVersion := "2.10.4"

logLevel := Level.Warn

resolvers += "sbt plugins" at "https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.3")

addSbtPlugin("com.vmunier" % "sbt-play-scalajs" % "0.2.8")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.0")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")
