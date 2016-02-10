name := "algocore"

organization := "it.datatoknowledge"

version := "0.1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-target:jvm-1.7", "-feature")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io",
  Resolver.sonatypeRepo("public"),
  Resolver.typesafeRepo("releases"),
  Resolver.jcenterRepo
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
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  "com.github.nscala-time" %% "nscala-time" % "2.8.0",
  "com.github.crawler-commons" % "crawler-commons" % "0.6",
  "org.apache.opennlp" % "opennlp-tools" % "1.6.0",
  "com.iheart" %% "ficus" % "1.2.0",
  "com.typesafe" % "config" % "1.3.0",
  "com.sksamuel.elastic4s" %% "elastic4s-core" % "2.2.0",
  "com.sksamuel.elastic4s" %% "elastic4s-jackson" % "2.2.0",
  "com.sksamuel.elastic4s" %% "elastic4s-streams" % "2.2.0"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

defaultScalariformSettings
