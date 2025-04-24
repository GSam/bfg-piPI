import cats._
import cats.data._
import cats.implicits._

sealed trait Console[A]
case class ReadLine[A](next: String => A) extends Console[A]
case class PrintLine[A](line: String, next: A) extends Console[A]

object Console {
  implicit val consoleComonad: Comonad[Console] =
    new Comonad[Console] {
      def extract[A](ca: Console[A]): A = ca match {
        case ReadLine(next) => next(scala.io.StdIn.readLine())
        case PrintLine(line, next) => println(line); next
      }
      def coflatMap[A, B](ca: Console[A])(f: Console[A] => B): Console[B] = ca match {
        case ReadLine(next) => ReadLine(_ => f(ca))
        case PrintLine(line, next) => PrintLine(line, coflatMap(next)(f))
      }
    }
}

val program: Cofree[Console, Unit] = Cofree(
  PrintLine("What's your name?", ReadLine(name =>
    PrintLine(s"Hello, $name!", Cofree(
      PrintLine("What's your age?", ReadLine(age =>
        PrintLine(s"You are $age years old.", Cofree(
          PrintLine("Goodbye!", ().pure[Console]))))))))))

val interpreter: Console ~> Id = new (Console ~> Id) {
  def apply[A](fa: Console[A]): A = fa.extract
}

val result: Unit = program.cata(interpreter)

