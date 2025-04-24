import cats._
import cats.data._
import cats.free._
import cats.implicits._

//import cats.Comonad.AllOps

sealed trait AdderF[A]
case class Add[A](toAdd: Int, next: Boolean => A) extends AdderF[A]
case class Clear[A](next: A) extends AdderF[A]
case class Total[A](next: Int => A) extends AdderF[A]

implicit val AdderFFunctor: Functor[AdderF] = new Functor[AdderF] {
  def map[A, B](fa: AdderF[A])(f: A => B): AdderF[B] = fa match {
      case Add(toAdd, next) => Add(toAdd, (f compose next))
      case Clear(next) => Clear(f(next))
      case Total(next) => Total(f compose next)
  }
}

type Adder[A] = Free[AdderF, A]

def add[T](toAdd: Int): Adder[Boolean] =
  Free.liftF[AdderF, Boolean](Add(toAdd, identity))

def clear: Adder[Unit] =
  Free.liftF[AdderF, Unit](Clear(()))

def total[T]: Adder[Int] =
  Free.liftF[AdderF, Int](Total(identity))


def test: Adder[Int] =
  for {
    _ <- add(1)
    _ <- add(2)
    n <- total
  } yield n


def test1: Adder[Int] =
  for {
    m <- add(1)
    _ <- add(if !m then 2 else 3)
    n <- total
  } yield n

def findLimit: Adder[Int] =
  for {
    t <- total
    _ <- clear
    // todo 
    _ <- clear
    _ <- add(t)
  } yield t

/*def findLimitS: Adder[Int] =
  for {
    t <- total
    _ <- clear
    r <- findL.runA(0)
    _ <- clear
    _ <- add(t)
  } yield t

def findL: StateT[Adder, Int, Unit] =
  for {
    a <- add(1)
    if (!a) {a} else {StateT.modifyF[Int, Adder](_ + 1); findL}
  } yield a
*/
  

/*case class CoAdderF[A](addH: Int => (Boolean, A),
                       clearH: A,
                       totalH: (Int, A))*/

case class CoAdderF[A](addH: Int => (Boolean, A),
                       clearH: A,
                       totalH: (Int, A))

implicit val CoAdderFFunctor: Functor[CoAdderF] = new Functor[CoAdderF] {
  def map[A, B](fa: CoAdderF[A])(f: A => B): CoAdderF[B] =
    CoAdderF(fa.addH.map((_).map(f)), f(fa.clearH), (fa.totalH._1, f(fa.totalH._2)))
}

type Limit = Int
type Count = Int

type CoAdder[A] = Cofree[CoAdderF, A]

def coClear(limitCount: (Limit, Count)): (Limit, Count) = (limitCount._1, 0)
//def coClear(limit: Limit, count: Count): (Limit, Count) = (limit, 0)
def coTotal(limitCount: (Limit, Count)): (Count, (Limit, Count)) = (limitCount._2, (limitCount._1, limitCount._2))

def coAdd(limitCount: (Limit, Count))(add: Int): (Boolean, (Limit, Count)) = {
  val newCount = limitCount._2 + add
  val test = newCount <= limitCount._1
  (test, (limitCount._1, if test then newCount else limitCount._2))
}
//type blah = (Limit, Count)
def mkCoAdder(limit: Limit, count: Count): CoAdder[(Limit, Count)] = {
  //val w: (Limit, Count) = (limit, count)
  //Cofree.unfold[CoAdderF, (Limit, Count)](w)((y: (Limit, Count)) => CoAdderF(coAdd(y),(coClear _).tupled(y),coTotal(y)))
  Cofree.unfold[CoAdderF, (Limit, Count)](limit, count)((y: (Limit, Count)) => CoAdderF(coAdd(y),coClear(y),coTotal(y)))
}

trait Pairing[F[_], G[_]]:
  def pair[A,B,R](fun: A => B => R)(f: F[A])(g: G[B]): R

implicit val IdentityPairing: Pairing[Id, Id] = new Pairing[Id, Id] {
  def pair[A,B,R](fun: A => B => R)(f: Id[A])(g: Id[B]): R = {
    fun(f)(g)
  }
}

import cats.arrow.FunctionK
import cats.{Id, ~>}

def impureCompiler: AdderF ~> Id =
  new (AdderF ~> Id) {
    var kvs = mkCoAdder(100, 0)

    def apply[A](fa: AdderF[A]): Id[A] =
      fa match {
        case Add(x, k) =>
          println("Add")
          var head, kv = kvs.tail.value.addH(x)
          kvs = kv._2
          (k(head._1))
        case Clear(k) =>
          println("Clear")
          val head, kv = kvs.tail.value.clearH
          kvs = kv
          (k)
        case Total(k) =>
          println("Total")
          val head, kv = kvs.tail.value.totalH
          kvs = kv._2
          (k(head._1))
      }
  }

/* test.foldMap(impureCompiler) */
/* Cofree.unfold[Option, Int](0)(i => if (i == 100) None else Some(i + 1)) */

/*implicit val FuncPair: Pairing[Function1[A, _], Tuple2[A,_]] = new Pairing[Function1[_, _], Tuple2[_,_]] {
  def pair[A,B,R](fun: A => B => R)(f: Function1[A,_])(g: Tuple2[B,_]): R = {

  }
}*/

// mkCoAdder(100, 10)._2.value.addH(10)._2._2.value.addH(15)._2._2.value.totalH._1

/*def impureCompiler: AdderF => Int = {

  var current: Int = 0

  def apply[A](fa: AdderF[A]): Int =
    fa match {
      case Add(toAdd, next) =>
        println(s"add($toAdd)")
        current = current + toAdd
        current
      case Clear(next) =>
        println(s"clear()")
        current = 0
        current
      case Total(next) =>
        println(s"total()")
        current
    }
}


def Main() =
  test().foldMap(impureCompiler)*/
