name := "qualitate"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick"   % "3.0.0",
  "org.slf4j" % "slf4j-api"         % "1.7.7",
  "org.slf4j" % "jcl-over-slf4j"    % "1.7.7",
  "com.h2database" % "h2"           % "1.3.175",
  "org.scalatest" %% "scalatest"    % "2.2.4" % "test",
  "org.apache.poi" % "poi"          % "3.13",
  "org.apache.poi" % "poi-excelant" % "3.13",
  "postgresql" % "postgresql"       % "9.1-901-1.jdbc4",
  jdbc,
  cache,
  ws,
  specs2 % Test
).map(_.force())

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-jdk14")) }


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
