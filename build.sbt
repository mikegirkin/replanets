lazy val root = (project in file(".")).
  settings(
    name := "replanets",
    version := "1.0.0",
    scalaVersion := "2.11.4"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
