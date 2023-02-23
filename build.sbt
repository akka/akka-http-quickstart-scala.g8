// This build is for this Giter8 template.
// To test the template run `g8` or `g8Test` from the sbt session.
// See http://www.foundweekends.org/giter8/testing.html#Using+the+Giter8Plugin for more details.
lazy val root = project
  .in(file("."))
  .enablePlugins(ScriptedPlugin)
  .settings(
    name := "pekko-http-scala-seed.g8",
    Test / test := {
      val _ = (Test / g8Test).toTask("").value
    },
    scriptedLaunchOpts ++= List("-Xms1024m", "-Xmx1024m", "-XX:ReservedCodeCacheSize=128m", "-XX:MaxMetaspaceSize=256m", "-Xss2m", "-Dfile.encoding=UTF-8"),
    resolvers += Resolver.typesafeIvyRepo("releases")
  )

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/main/index.html
lazy val docs = project
  .in(file("docs"))
  .enablePlugins(ParadoxPlugin)
  .settings(
    resolvers += Resolver.typesafeIvyRepo("releases")
  )
