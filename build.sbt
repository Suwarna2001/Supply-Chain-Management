ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

// Use the mainClass setting for running the application
Compile / run / mainClass := Some("play.core.server.ProdServerStart")

lazy val root = (project in file("."))
  .settings(
    name := "SupplyChainManagement",
    idePackagePrefix := Some("scalaProject")
  )

libraryDependencies += "com.typesafe.play" %% "play" % "2.9.0"
libraryDependencies += "com.typesafe.play" %% "play-test" % "2.9.0" % Test
libraryDependencies += "com.typesafe.play" %% "play-server" % "2.9.0"
libraryDependencies += "com.typesafe.play" %% "play-logback" % "2.9.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.4"
libraryDependencies += "com.typesafe.play" %% "play-akka-http-server" % "2.9.0"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.9.0-M2"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.9.0-M2" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.9.0-M2"
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.9.0-M2"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.6.0-M1"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.6.0-M1"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.14" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
libraryDependencies += "org.postgresql" % "postgresql" % "42.7.2"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.5.0-RC1"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.6.1"
libraryDependencies += "com.typesafe.play" %% "twirl-api" % "1.6.4"
libraryDependencies += "com.google.inject" % "guice" % "5.0.1"
libraryDependencies += "com.google.inject.extensions" % "guice-assistedinject" % "5.0.1"


// Add the conf directory to the resources directories
Compile / unmanagedResourceDirectories += baseDirectory.value / "conf"
