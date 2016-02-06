name := "algocore"

organization := "it.datatoknowledge"

version := "0.1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-target:jvm-1.7", "-feature")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io",
  Resolver.sonatypeRepo("public"),
  Resolver.typesafeRepo("releases")
)

libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.8.0",
  "org.jsoup" % "jsoup" % "1.8.3",
  "com.typesafe.play" %% "play-ws" % "2.4.6",
  "com.intenthq" %% "gander" % "1.3",
  "com.rometools" % "rome" % "1.5.1",
  "com.syncthemall" % "boilerpipe" % "1.2.2",
  "org.apache.tika" % "tika-core" % "1.10",
  "org.apache.tika" % "tika-parsers" % "1.10",
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "org.json4s" %% "json4s-ext" % "3.3.0",
  "com.github.nscala-time" %% "nscala-time" % "2.8.0",
  "com.github.crawler-commons" % "crawler-commons" % "0.6"
)

defaultScalariformSettings
