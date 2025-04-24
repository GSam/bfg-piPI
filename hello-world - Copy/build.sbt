//val scala3Version = "3.3.0"
val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Hello World",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.3.8",
    libraryDependencies += ("com.eed3si9n.eval" % "eval" % "0.3.0").cross(CrossVersion.full),
    //   libraryDependencies += "org.scala-lang" %% "scala3-compiler" % "3.3.0"
    //   libraryDependencies += "org.scala-lang" %% "scala3-reflect" % "2.12.0"
    fork := true
  )
