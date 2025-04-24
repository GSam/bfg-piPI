import cats._
import cats.data._
import cats.free._
import cats.implicits._

//import cats.Comonad.AllOps

sealed trait AddF[A]
case class Ad[A](arg1: A, arg2: A) extends AddF[A]
case class Mult[A](arg1: A, arg2: A) extends AddF[A]
case class One[A]() extends AddF[A]

implicit val AddFFunctor: Functor[AddF] = new Functor[AddF] {
  def map[A, B](fa: AddF[A])(f: A => B): AddF[B] = fa match {
      case Ad(arg1, arg2) => Ad(f(arg1), f(arg2))
      case Mult(arg1, argx) => Mult(f(arg1), f(argx))
      case One() => One()
  }
}

type FAdd[A] = Free[AddF, A]
type CoFAdd[A] = Cofree[AddF, A]

val xx: FAdd[Int] = Free.liftF[AddF, Int](Ad(1,2))
val yy: FAdd[Int] = Free.liftF[AddF, Int](One())

val zz: CoFAdd[Int] = Cofree.unfold(3)(x => if (x > 1) { Mult(2, 2)} else {One()})
