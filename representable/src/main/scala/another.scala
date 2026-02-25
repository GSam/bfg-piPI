// scala-cli dep org.typelevel::cats-core:2.10.0

import cats._
import cats.syntax.all._
import cats.Eval
import scala.collection.mutable.ListBuffer

object RepresentableMemoize {

  // ==========================================
  // 1. Data Structures
  // ==========================================

  case class Stream[A](head: A, tail: Eval[Stream[A]])
  case class Tree[A](root: A, left: Eval[Tree[A]], right: Eval[Tree[A]])

  // ==========================================
  // 2. Functor Instances
  // ==========================================

  given Functor[Stream] with {
    def map[A, B](fa: Stream[A])(f: A => B): Stream[B] =
      Stream(f(fa.head), fa.tail.map(s => map(s)(f)))
  }

  given Functor[Tree] with {
    def map[A, B](fa: Tree[A])(f: A => B): Tree[B] =
      Tree(
        f(fa.root),
        fa.left.map(l => map(l)(f)),
        fa.right.map(r => map(r)(f))
      )
  }

  // ==========================================
  // 3. Representable Instances
  // ==========================================

  given Representable[Stream] with {
    type Representation = Int
    override val F: Functor[Stream] = summon[Functor[Stream]]

    override def tabulate[A](f: Int => A): Stream[A] = {
      def go(n: Int): Stream[A] = Stream(
        f(n),
        Eval.later(go(n + 1))
      )
      go(0)
    }

    override def index[A](fa: Stream[A]): Int => A = n => {
      def go(s: Stream[A], idx: Int): A = 
        if (idx == 0) s.head
        else go(s.tail.value, idx - 1)
      go(fa, n)
    }
  }

  given Representable[Tree] with {
    type Representation = Int
    override val F: Functor[Tree] = summon[Functor[Tree]]

    override def tabulate[A](f: Int => A): Tree[A] = {
      def go(n: Int): Tree[A] = Tree(
        f(n),
        Eval.later(go(2 * n + 1)), // Left child (Odd indices)
        Eval.later(go(2 * n + 2))  // Right child (Even indices)
      )
      go(0)
    }

    /**
     * Correct Indexing for BFS Tree:
     * To find n, we calculate the path from n back to 0.
     * - If current k is odd, it came from the Left of (k-1)/2.
     * - If current k is even, it came from the Right of (k-1)/2.
     */
    override def index[A](fa: Tree[A]): Int => A = n => {
      // 1. Determine path from leaf n up to root 0
      var curr = n
      var path: List[Boolean] = Nil // true = Left, false = Right
      
      while (curr > 0) {
        val isLeft = (curr % 2 != 0)
        path = isLeft :: path
        curr = (curr - 1) / 2
      }

      // 2. Traverse down from the root
      var t = fa
      var steps = path
      while (steps.nonEmpty) {
        if (steps.head) t = t.left.value
        else t = t.right.value
        steps = steps.tail
      }
      t.root
    }
  }

  // ==========================================
  // 4. Fibonacci & Memoization Logic
  // ==========================================

  // Open recursion
  def fibOp(rec: Int => BigInt)(n: Int): BigInt = n match {
    case 0 => 0
    case 1 => 1
    case _ => rec(n - 1) + rec(n - 2)
  }

  // Memoization Wrapper
  def memoize[F[_], A](g: (Int => A) => (Int => A))
                      (using R: Representable[F] { type Representation = Int }): Int => A = {
    // We pass `k => R.index(store)(k)` (eta-expansion) to delay evaluation of `store`
    lazy val store: F[A] = R.tabulate(k => g(n => R.index(store)(n))(k))
    R.index(store)
  }

  val fibNaive: Int => BigInt = {
    @scala.annotation.nowarn
    lazy val f: Int => BigInt = n => fibOp(f)(n)
    f
  }

  val fibStream: Int => BigInt = memoize[Stream, BigInt](fibOp)
  val fibTree: Int => BigInt   = memoize[Tree, BigInt](fibOp)

  // ==========================================
  // 5. Main
  // ==========================================
  
  def main(args: Array[String]): Unit = {
    println("--- Fibonacci Memoization with Representable ---")
    
    val n = 45

    println(s"Calculating fib($n)...")
    // Naive 
    val rNaive = time { fibNaive(n) }
    println(s"1. Naive Recursion Result: $rNaive")
    
    // Stream
    val rStream = time { fibStream(n) }
    println(s"2. Stream Memoized Result: $rStream")
    
    // Tree
    val rTree = time { fibTree(n) }
    println(s"3. Tree Memoized Result:   $rTree")

    assert(rStream == rNaive, "Stream result mismatch")
    assert(rTree == rNaive, "Tree result mismatch")

    println("\n--- Structure Verification ---")
    val natTree = summon[Representable[Tree]].tabulate(identity)
    
    // Check specific indices to verify navigation logic
    // Root(0) -> Left(1) -> Right(4) should be 4
    val idx4 = summon[Representable[Tree]].index(natTree)(4)
    println(s"Index 4 (Should be 4): $idx4")
    
    // Root(0) -> Right(2) -> Left(5) should be 5
    val idx5 = summon[Representable[Tree]].index(natTree)(5)
    println(s"Index 5 (Should be 5): $idx5")
  }



  import scala.concurrent.duration.*

  /**
   * A simple utility to time a block of code.
   * @param block The code block to execute and time.
   * @tparam A The return type of the block.
   * @return The result of the block.
   */
  def time[A](block: => A): A = {
    val startTime = System.nanoTime()
    val result = block // Execute the code block
    val endTime = System.nanoTime()

    val duration = (endTime - startTime).nanos

    println(s"Elapsed time: ${duration.toMillis} ms (${duration.toNanos} ns)")

    result
  }
}

