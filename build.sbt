lazy val root = (project in file(".")).
  settings(
    name := "replanets",
    version := "1.0.0",
    scalaVersion := "2.11.4"
  )

unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar"))

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalafx" %% "scalafx" % "8.0.92-R10"
)