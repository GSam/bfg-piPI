import cats._
import cats.data._
import cats.free._
import cats.implicits._

val BOUND = (50, 30)

@main def hello: Unit =
  println("Hello world!")
  println(msg)

  print("\u001B[2J")


  //var initialState = Set((1, 0), (1, 1), (2,1), (5,1), (4,3), (4,4), (10,5), (9,5), (8,6))
  var initialState = Set((1, 0), (2, 1), (0, 2), (1, 2), (2, 2))
  var current = makeGrid(initialState)
  render(current)
  while(true) {
    current = step(current)
    render(current)
  }

// (x: (Int, Int)) => true

val adjacent = List(
    (-1,-1), (0,-1), (1,-1),
    (-1, 0),         (1, 0),
    (-1, 1), (0, 1), (1, 1)
)

def neighbourCoords(c: (Int, Int)): List[(Int, Int)] = {
    adjacent.map { (x: Int, y:Int) => (java.lang.Math.floorMod(c._1+x, BOUND._1), java.lang.Math.floorMod(c._2+y, BOUND._2)) }
}


case class BoundedGrid[A](data: List[A])

object Extensions {

implicit def RepresentableBoundedGrid(implicit
    PF: Functor[BoundedGrid]): Representable.Aux[BoundedGrid, (Int, Int)] =
    new Representable[BoundedGrid] {
      override type Representation = (Int, Int)
      override val F: Functor[BoundedGrid] = PF

      override def tabulate[A](f: ((Int, Int)) => A): BoundedGrid[A] = {
        var xs: List[A] = List()
        for (y <- 0 until BOUND._2) {
          for (x <- 0 until BOUND._1) {
            xs = xs ++ List(f((x, y)))
          }
        }
      
        BoundedGrid(xs)
     }

      override def index[A](bg: BoundedGrid[A]): Representation => A = {
         (pair: (Int, Int)) => {/*println(pair);*/ bg.data(pair._2*BOUND._1+pair._1)}
      }
    }
}

import Extensions._

implicit val BoundedGridFunctor: Functor[BoundedGrid] = new Functor[BoundedGrid] {
  def map[A, B](fa: BoundedGrid[A])(f: A => B): BoundedGrid[B] = fa match {
      case BoundedGrid(data) => BoundedGrid(data.map(f))
  }
}


def msg = "I was compiled by Scala 3. :)"

//type Grid = Store[(Int, Int), Boolean]
type Grid = RepresentableStore[BoundedGrid, (Int, Int), Boolean]

def makeGrid(init: Set[(Int, Int)]): Grid = new RepresentableStore(RepresentableBoundedGrid.tabulate(init.contains), (0, 0))
//def makeGrid(init: Set[(Int, Int)]): Grid = Store(init.contains, (0, 0))

def step(grid: Grid): Grid = grid.coflatMap(conway)

def conway(grid: Grid): Boolean = {

  val alive = grid.extract

  val liveCount = grid.experiment(neighbourCoords).foldLeft(0)((x, y) => x + (if y then 1 else 0))
 
  if alive then (liveCount == 2 || liveCount == 3) else liveCount == 3
}

def render(grid: Grid) = {
  print("\u001B[2J") 
  var xs: List[(Int, Int)] = List()
  for (y <- 0 until BOUND._2) {
    for (x <- 0 until BOUND._1) {
      xs = xs ++ List((x, y))
    }
  }
  print(grid.experiment(_ => xs).map( x => if x then "P" else "." ).grouped(BOUND._1).map(_.mkString("")+"\n").mkString(""))   
  
  println("HELLLO") ; Thread.sleep(100) 

}