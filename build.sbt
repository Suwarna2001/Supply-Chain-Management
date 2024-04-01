lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-scala-fileupload-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.13", "3.3.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      "com.typesafe.play" %% "play" % "2.9.0",
      "com.typesafe.play" %% "play-ehcache" % "2.9.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
      "com.typesafe.akka" %% "akka-actor" % "2.8.5",
      "com.typesafe.akka" %% "akka-testkit" % "2.8.5" % Test,
      "com.typesafe.akka" %% "akka-stream" % "2.8.5",
      "com.typesafe.akka" %% "akka-slf4j" % "2.8.5",

      "ch.qos.logback" % "logback-classic" % "1.4.14" % Test,
      "org.postgresql" % "postgresql" % "42.7.1",
      "com.typesafe.slick" %% "slick" % "3.4.1",
      "org.apache.kafka" % "kafka-clients" % "3.6.0",

      "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.2",
      "com.typesafe.akka" %% "akka-http" % "10.5.3",

      "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3",
      "com.typesafe.play" %% "play-akka-http-server" % "2.9.0"
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    ),
    // Needed for ssl-config to create self signed certificated under Java 17
    Test / javaOptions ++= List("--add-exports=java.base/sun.security.x509=ALL-UNNAMED"),
  )
