name := "algocore"

organization := "it.datatoknowledge"

version := "0.2.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-target:jvm-1.8", "-feature")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io",
  Resolver.sonatypeRepo("public"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.typesafeRepo("releases"),
  Resolver.jcenterRepo
)

libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.10.0",
  "org.jsoup" % "jsoup" % "1.8.3",
  "com.typesafe.play" %% "play-ws" % "2.4.6",
  "com.rometools" % "rome" % "1.5.1",
  "org.apache.tika" % "tika-core" % "1.12",
  "org.apache.tika" % "tika-parsers" % "1.12",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  "com.iheart" %% "ficus" % "1.2.0",
  "org.apache.opennlp" % "opennlp-tools" % "1.6.0",
  "com.typesafe" % "config" % "1.3.0",
  "com.sksamuel.elastic4s" %% "elastic4s-core" % "2.2.0",
  "com.sksamuel.elastic4s" %% "elastic4s-jackson" % "2.2.0",
  "com.intenthq" %% "gander" % "1.3",
  "net.sandrogrzicic" %% "scalabuff-runtime" % "1.4.0"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)
