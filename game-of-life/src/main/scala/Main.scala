import scalaz._
import scalaz.Scalaz._

val BOUND = (50, 30)

@main def hello: Unit =
  println("Hello world!")
  println(msg)
  com.eed3si9n.eval.Eval()
    .evalInfer("""println("Hello, World! EVAL")""")
    .getValue(this.getClass.getClassLoader)

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
    adjacent.map { (x: Int, y:Int) => (c._1+x, c._2+y) }
}

type Grid = Store[(Int, Int), Boolean]

def msg = "I was compiled by Scala 3. :)"

def makeGrid(init: Set[(Int, Int)]): Grid = Store(init.contains, (0, 0))

def step(grid: Grid): Grid = grid.cobind(conway)

def conway(grid: Grid): Boolean = {

  val alive = grid.copoint

  val liveCount = grid.experiment(neighbourCoords).foldLeft(0)((x, y) => x + (if y then 1 else 0))
  //val liveCount = cachedexperiment(grid, neighbourCoords, grid.pos, neighbourCoords(grid.pos)).foldLeft(0)((x, y) => x + (if y then 1 else 0))
 
  if alive then (liveCount == 2 || liveCount == 3) else liveCount == 3
}

import scalacache._
import scalacache.caffeine._
import scalacache.memoization._
import com.github.benmanes.caffeine.cache.Caffeine


val underlyingCache = Caffeine.newBuilder().build[String, Entry[List[Boolean]]]()
implicit val catsCache: Cache[cats.effect.SyncIO, String, List[Boolean]] =  CaffeineCache[cats.effect.SyncIO, String, List[Boolean]](underlyingCache)

// System.identityHashCode(grid)

def cachedexperiment(@cacheKeyExclude grid: Grid, @cacheKeyExclude f: ((Int, Int)) => List[(Int, Int)], g: (Int, Int), h: List[(Int, Int)]): List[Boolean] = memoize[cats.effect.SyncIO, List[Boolean]](None){
    grid.experiment(f)
}.unsafeRunSync()

def render(grid: Grid) = {
  print("\u001B[2J") 
  var xs: List[(Int, Int)] = List()
  for (y <- 0 until BOUND._2) {
    for (x <- 0 until BOUND._1) {
      xs = xs ++ List((x, y))
    }
  }
  //print(grid.experiment(_ => xs).map( x => if x then "\u2587" else " " ))
  print(grid.experiment(_ => xs).map( x => if x then "P" else "." ).grouped(BOUND._1).map(_.mkString("")+"\n").mkString(""))
  //print(cachedexperiment(grid, _ => xs, grid.pos, xs).map( x => if x then "P" else "." ).grouped(BOUND._1).map(_.mkString("")+"\n").mkString(""))
    
  
  println("HELLLO") ; Thread.sleep(1000) 

}