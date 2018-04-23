// Uses the out of the box generic theme.
paradoxTheme := Some(builtinParadoxTheme("generic"))

scalaVersion := "2.12.5"

paradoxProperties in Compile ++= Map(
  "snip.g8root.base_dir" -> "../../../../src/main/g8",
  "snip.g8src.base_dir" -> "../../../../src/main/g8/src/main/",
  "snip.g8srctest.base_dir" -> "../../../../src/main/g8/src/test/",
  "extref.akka.scala.base_url" -> "https://doc.akka.io/docs/akka/current/%s",
  "extref.akka.http.scala.base_url" -> "https://doc.akka.io/docs/akka-http/current/%s"
)
