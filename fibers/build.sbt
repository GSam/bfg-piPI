val scala3Version = "3.8.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Scala 3 Project Template",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "dev.zio" %% "zio" % "2.1.9",
    libraryDependencies += "com.softwaremill.sttp.client3" %% "zio" % "3.9.0",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.4"
  )
