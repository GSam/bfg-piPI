## sbt project compiled with Scala 3

scala> import zio._

scala> val runtime = Runtime.default

scala> Unsafe.unsafe { implicit unsafe => runtime.unsafe.run(ZioFibersTutorial1.run).getOrThrowFiberFailure() }

### Usage

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).
