import sbt._

object Dependencies {
  lazy val akkaHttpVersion = "$akka_http_version$"
  lazy val akkaHttp        = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  lazy val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
  lazy val scalaTest       = "org.scalatest" %% "scalatest" % "3.0.1"
}
