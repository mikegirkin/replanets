addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)


lazy val root = (project in file(".")).
  settings(
    name := "replanets",
    version := "1.0.0",
    scalaVersion := "2.11.8"
  )

unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar"))

libraryDependencies ++= Seq(
  "org.scalafx"       %% "scalafx"             % "8.0.92-R10",
  "org.scalafx"       %% "scalafxml-core-sfx8" % "0.2.2",
  "com.typesafe"      %  "config"              % "1.3.0",
  "com.typesafe.play" %% "play-json"           % "2.5.4",

  "org.scalatest" %% "scalatest"   % "3.0.0"   % "test",
  "org.mockito"   % "mockito-core" % "1.10.19" % "test"

)