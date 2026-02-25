import cats.*
import cats.implicits.*
import cats.free.Cofree
import scala.collection.immutable.LazyList
import cats.kernel.CommutativeMonoid

// Definition for the Sum monoid.
case class Sum[A](value: A)
object Sum {
  given[A](using N: Numeric[A]): CommutativeMonoid[Sum[A]] with
    def empty: Sum[A] = Sum(N.zero)
    def combine(x: Sum[A], y: Sum[A]): Sum[A] = Sum(N.plus(x.value, y.value))
}

// 1. Representable Functor Trait
trait Representable[F[_]]:
  type Rep
  def index[A](fa: F[A])(rep: Rep): A
  def tabulate[A](f: Rep => A): F[A]

// 2. MRep Opaque Type
opaque type MRep[F[_], A] = F[A]
object MRep:
  def apply[F[_], A](fa: F[A]): MRep[F, A] = fa
  extension [F[_], A](mrep: MRep[F, A]) def unMRep: F[A] = mrep

  given[F[_]: Representable, A: Monoid]: Monoid[MRep[F, A]] with
    val R = summon[Representable[F]]
    val M = summon[Monoid[A]]
    def empty: MRep[F, A] = MRep(R.tabulate(_ => M.empty))
    def combine(x: MRep[F, A], y: MRep[F, A]): MRep[F, A] =
      MRep(R.tabulate(rep => R.index(x.unMRep)(rep) |+| R.index(y.unMRep)(rep)))

// 3. repSort function
// Corrected signature to handle path-dependent types by adding a type parameter 'R'.
def repSort[F[_], G[_], M, A, R](
    ga: G[A]
)(
    indOf: A => R
)(
    toM: A => M
)(using
    F: Representable[F] { type Rep = R },
    G: Foldable[G],
    M: Monoid[M],
    E: Eq[R]
): F[M] =
  val desc: A => (R => M) = a => i => if E.eqv(i, indOf(a)) then toM(a) else M.empty
  val toMRep: A => MRep[F, M] = a => MRep(F.tabulate(desc(a)))
  import MRep.unMRep
  import MRep.given_Monoid_MRep
  G.foldMap(ga)(toMRep).unMRep

// 4. Pair data type and its Representable instance
case class Pair[A](a: A, b: A)

given Representable[Pair] with
  type Rep = Boolean
  def index[A](fa: Pair[A])(rep: Boolean): A = if rep then fa.a else fa.b
  def tabulate[A](f: Boolean => A): Pair[A] = Pair(f(true), f(false))

// 5. Representable instance for LazyList
given Representable[LazyList] with
  type Rep = Int
  def index[A](fa: LazyList[A])(rep: Int): A = fa(rep)
  def tabulate[A](f: Int => A): LazyList[A] = LazyList.from(0).map(f)

// 6. Representable instance for Cofree[LazyList, _] to create a Trie
given cofreeRepresentable: Representable[[A] =>> Cofree[LazyList, A]] with
  type Rep = Seq[Int]
  def index[A](fa: Cofree[LazyList, A])(rep: Rep): A = rep match
    case Seq() => fa.head
    case r +: rs => index(fa.tailForced(r))(rs)
  def tabulate[A](f: Rep => A): Cofree[LazyList, A] =
    def build(path: Rep): Cofree[LazyList, A] =
      Cofree(f(path), Eval.later(LazyList.from(0).map(i => build(path :+ i))))
    build(Seq.empty)

// 7. Main object to run the examples
@main def RepresentableSorting(): Unit =

  println("--- Sorting odd and even numbers ---")
  // Updated calls to repSort with the new type parameter 'R' to guide the compiler.
  val sortedInts = repSort[Pair, List, List[Int], Int, Boolean](
    List.range(1, 11)
  )(
    (i: Int) => i % 2 != 0
  )(
    List(_)
  )
  println(s"sortedInts: $sortedInts")

  val oddEvenSums = repSort[Pair, List, Sum[Int], Int, Boolean](
    List.range(1, 11)
  )(
    (i: Int) => i % 2 != 0
  )(
    i => Sum(i)
  )
  println(s"oddEvenSums: $oddEvenSums")
  println("-" * 20)

  println("--- Sorting strings by length ---")
  val byLength = repSort[LazyList, List, List[String], String, Int](
    List("javascript", "purescript", "haskell", "python")
  )(
    (s: String) => s.length
  )(
    List(_)
  )
  println(s"Strings of length 10: ${byLength(10)}")
  println(s"Strings of length 7: ${byLength(7)}")
  println(s"Strings of length 3: ${byLength(3)}")
  println("-" * 20)

  println("--- Sorting strings by first character ---")
  val byFirstChar = repSort[LazyList, List, List[String], String, Int](
    List("cats", "antelope", "crabs", "aardvarks")
  )(
    (s: String) => s.head.toInt
  )(
    List(_)
  )
  println(s"Strings starting with 'c': ${byFirstChar('c'.toInt)}")
  println(s"Strings starting with 'a': ${byFirstChar('a'.toInt)}")
  println(s"Strings starting with 'z': ${byFirstChar('z'.toInt)}")
  println("-" * 20)

  println("--- Trie-based Map ---")
  def mkInd(s: String): Seq[Int] = s.map(_.toInt).toSeq

  def trieSort[M: Monoid, G[_]: Foldable, A](
      fa: G[A]
  )(
      getInd: A => String
  )(
      toM: A => M
  ): Cofree[LazyList, M] = {
    type F[B] = Cofree[LazyList, B]
    // Provide an Eq instance for the representation type 'R' (which is Seq[Int]).
    given Eq[Seq[Int]] = Eq.fromUniversalEquals
    repSort[F, G, M, A, Seq[Int]](fa)(mkInd.compose(getInd))(toM)
  }

  def trieMap[M: Monoid](kvs: List[(String, M)]): Cofree[LazyList, M] =
    trieSort(kvs)(_._1)(_._2)

  def get[A](r: Cofree[LazyList, A])(ind: String): A =
    cofreeRepresentable.index(r)(mkInd(ind))

  val bankAccounts = trieMap(List("Bob" -> Sum(37), "Sally" -> Sum(5)))
  println(s"Bob's account: ${get(bankAccounts)("Bob")}")
  println(s"Sally's account: ${get(bankAccounts)("Sally")}")
  println(s"Edward's account: ${get(bankAccounts)("Edward")}")

  val withdrawals = trieMap(List("Bob" -> Sum(-10)))
  val combined = MRep(bankAccounts) |+| MRep(withdrawals)

  println(s"Bob's account after withdrawal: ${get(combined.unMRep)("Bob")}")
  println("-" * 20)
