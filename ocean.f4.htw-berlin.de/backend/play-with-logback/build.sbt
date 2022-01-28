name := """play-with-logback"""
organization := "com.htwhub"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.5"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "7.0.1"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.1"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.htwhub.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.htwhub.binders._"
