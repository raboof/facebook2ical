resolvers += Resolver.sonatypeRepo("public")

scalacOptions := Seq("-feature", "-deprecation")

scalaVersion := "2.12.1"

//scalafmtConfig in ThisBuild := Some(file(".scalafmt"))

libraryDependencies += "net.bzzt" %% "scala-icalendar" % "0.0.1-SNAPSHOT"
libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.3.0"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"
