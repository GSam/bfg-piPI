import zio._

object ZioFibersTutorial extends ZIOAppDefault {

  // Helper method to simulate delay
  private def delay(duration: Duration): ZIO[Any, Nothing, Unit] = ZIO.sleep(duration)

  // Cooking actions
  private def boilWater(): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println("Water put on stove..."))
    _ <- delay(100.millis)
    _ <- ZIO.succeed(println("Water boiled!"))
  } yield ()

  private def boilPasta(): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println("Put pasta in boiling water..."))
    _ <- delay(1.second)
    _ <- ZIO.succeed(println("Pasta ready!"))
  } yield ()

  private def prepareIngredient(ingredient: String): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println(s"Preparing $ingredient..."))
    _ <- delay(300.millis)
    _ <- ZIO.succeed(println(s"$ingredient ready"))
  } yield ()

  private def makeSauce(): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println(s"Preparing sauce..."))
    _ <- delay(1.second)
    _ <- ZIO.succeed(println(s"Sauce is ready"))
  } yield ()

  // Ordering food action (introduced later in the tutorial)
  private def orderFood(): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println("Ordering some food..."))
    _ <- delay(1.second)
    _ <- ZIO.succeed(println("Food arrived!"))
  } yield ()

  // Cleanup action (finalizer)
  private val cleanup: ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println(s"Cleaning up kitchen and delivery packages..."))
    _ <- delay(10.millis)
    _ <- ZIO.succeed(println("All clean"))
  } yield ()

  // The final run logic combining all concepts:
  // Forking, Joining, Dependencies (await/zip), Interruption, and Finalizers
  override def run: ZIO[Any, Any, Any] = {
    for {
      // Start boiling water in a fiber
      waterFiber <- boilWater().fork
      
      // Wait for water to boil, then boil pasta in a fiber
      pastaFiber <- waterFiber.await.zip(boilPasta()).fork
      
      // Start preparing tomato
      tomatoFiber <- prepareIngredient("tomato").fork
      
      // Wait for tomato, then prepare onion
      onionFiber <- tomatoFiber.await.zip(prepareIngredient("onion")).fork
      
      // Wait for onion, then make sauce
      sauceFiber <- onionFiber.await.zip(makeSauce()).fork
      
      // Interrupt the pasta cooking process (pasta + sauce) because we changed our mind
      _ <- pastaFiber.zip(sauceFiber).interrupt
      
      // Order food instead
      orderFiber <- orderFood().fork
      
      // Wait for food to arrive and ensure cleanup happens afterwards
      _ <- orderFiber.join.ensuring(cleanup)
    } yield ()
  }
}

object ZioFibersTutorial1 extends ZIOAppDefault {

  // --- Helpers ---
  private def delay(duration: Duration): ZIO[Any, Nothing, Unit] = ZIO.sleep(duration)

  // --- Cooking Actions ---
  private def boilWater(): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println("Water put on stove..."))
    _ <- delay(500.millis)
    _ <- ZIO.succeed(println("Water boiled!"))
  } yield ()

  private def boilPasta(): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println("Put pasta in boiling water..."))
    _ <- delay(500.millis)
    _ <- ZIO.succeed(println("Pasta ready!"))
  } yield ()

  private def makeSauce(): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println(s"Preparing sauce..."))
    _ <- delay(500.millis)
    _ <- ZIO.succeed(println(s"Sauce is ready"))
  } yield ()

  // Combine individual cooking steps into one "Cooking" task
  private val cookPastaDish: ZIO[Any, Nothing, String] = for {
    _ <- boilWater()
    // Run pasta and sauce in parallel
    f1 <- boilPasta().fork
    f2 <- makeSauce().fork
    _  <- f1.join.zip(f2.join)
  } yield "Homemade Pasta"

  // --- Ordering Actions ---
  private val orderTakeout: ZIO[Any, Nothing, String] = for {
    _ <- ZIO.succeed(println("Ordering some food..."))
    // Change this delay to test who wins!
    // If < 1000ms, Delivery wins. If > 1000ms, Cooking wins.
    _ <- delay(1800.millis)
    _ <- ZIO.succeed(println("Food arrived!"))
  } yield "Delivery Pizza"

  // --- Cleanup ---
  private val cleanup: ZIO[Any, Nothing, Unit] =
    ZIO.succeed(println("Cleaning up the kitchen..."))

  // --- Main Run ---
  override def run: ZIO[Any, Any, Any] = {

    // We race cooking vs ordering
    cookPastaDish.raceWith(orderTakeout)(

      // Case 1: Left (Cooking) finishes first
      (exitLeft, fiberRight) => exitLeft match {
        case Exit.Success(dish) =>
          for {
            _ <- fiberRight.interrupt // Cancel the order
            _ <- ZIO.succeed(println(s"RACE FINISHED: We are eating $dish"))
          } yield ()
        case Exit.Failure(cause) =>
          // If cooking fails, wait for delivery
          fiberRight.join.flatMap(dish => ZIO.succeed(println(s"Cooking failed. We are eating $dish")))
      },

      // Case 2: Right (Ordering) finishes first
      (exitRight, fiberLeft) => exitRight match {
        case Exit.Success(dish) =>
          for {
            _ <- fiberLeft.interrupt // Stop cooking immediately
            _ <- ZIO.succeed(println(s"RACE FINISHED: We are eating $dish"))
          } yield ()
        case Exit.Failure(cause) =>
          // If ordering fails, wait for cooking
          fiberLeft.join.flatMap(dish => ZIO.succeed(println(s"Delivery failed. We are eating $dish")))
      }
    ).ensuring(cleanup)
  }
}

import zio._
import sttp.client3._
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.client3.httpclient.zio._

object ZioFibersTutorial2 extends ZIOAppDefault {

  // Type alias for cleaner signatures
  type ZioClient = SttpClient

  // --- Helper: HTTP Request instead of Sleep ---
  // We use httpbin.org to force the network request to take 'seconds' to respond.
  def performTask(name: String, seconds: Int): ZIO[ZioClient, Throwable, Unit] = {
    val request = basicRequest.get(uri"https://httpbin.org/delay/$seconds")

    for {
      _ <- ZIO.succeed(println(s"[$name] Request sent (waiting ${seconds}s)..."))
      _ <- send(request) // Performs the actual HTTP request
      _ <- ZIO.succeed(println(s"[$name] Response received!"))
    } yield ()
  }

  // --- Cooking Process (Approx 4 seconds total) ---
  // 1. Boil Water (2s)
  // 2. Parallel: Pasta (2s) & Sauce (2s)
  val cookPastaDish: ZIO[ZioClient, Throwable, String] = for {
    _ <- performTask("Boil Water", 2)

    // Fork pasta and sauce to run them at the same time
    pastaFiber <- performTask("Boil Pasta", 2).fork
    sauceFiber <- performTask("Make Sauce", 2).fork

    // Wait for both
    _ <- pastaFiber.join.zip(sauceFiber.join)
  } yield "Homemade Pasta"

  // --- Ordering Process (Variable Time) ---
  // Change the seconds here to change the winner!
  // If 1 second: Delivery wins.
  // If 6 seconds: Cooking wins.
  val orderTakeout: ZIO[ZioClient, Throwable, String] = for {
    _ <- performTask("Order Pizza", 6)
  } yield "Delivery Pizza"

  // --- Cleanup ---
  val cleanup: ZIO[Any, Nothing, Unit] =
    ZIO.succeed(println("--- Cleanup: Washing dishes / Throwing away boxes ---"))

  // --- Main Run ---
  override def run: ZIO[Any, Any, Any] = {

    val raceLogic = cookPastaDish.raceWith(orderTakeout)(
      // Case 1: Left (Cooking) wins
      (exitLeft, fiberRight) => exitLeft match {
        case Exit.Success(dish) =>
          fiberRight.interrupt *> ZIO.succeed(println(s"\nWINNER: We are eating $dish"))
        case Exit.Failure(e) =>
          fiberRight.join.flatMap(dish => ZIO.succeed(println(s"\nCooking failed ($e). Eating $dish")))
      },
      // Case 2: Right (Ordering) wins
      (exitRight, fiberLeft) => exitRight match {
        case Exit.Success(dish) =>
          fiberLeft.interrupt *> ZIO.succeed(println(s"\nWINNER: We are eating $dish"))
        case Exit.Failure(e) =>
          fiberLeft.join.flatMap(dish => ZIO.succeed(println(s"\nDelivery failed ($e). Eating $dish")))
      }
    )

    // Wire everything together
    raceLogic
      .ensuring(cleanup)
      .provide(HttpClientZioBackend.layer()) // Inject the HTTP backend
  }
}

object ZioFibersTutorial3 extends ZIOAppDefault {

  // Helper method to simulate delay
  private def delay(duration: Duration): ZIO[Any, Nothing, Unit] = ZIO.sleep(duration)

  // Cooking actions
  private def boilWater(): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println("Water put on stove..."))
    _ <- delay(100.millis)
    _ <- ZIO.succeed(println("Water boiled!"))
  } yield ()

  private def boilPasta(): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println("Put pasta in boiling water..."))
    _ <- delay(1.second)
    _ <- ZIO.succeed(println("Pasta ready!"))
  } yield ()

  private def prepareIngredient(ingredient: String): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println(s"Preparing $ingredient..."))
    _ <- delay(300.millis)
    _ <- ZIO.succeed(println(s"$ingredient ready"))
  } yield ()

  private def makeSauce(): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println(s"Preparing sauce..."))
    _ <- delay(1.second)
    _ <- ZIO.succeed(println(s"Sauce is ready"))
  } yield ()

  // Ordering food action (introduced later in the tutorial)
  private def orderFood(): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println("Ordering some food..."))
    _ <- delay(1.second)
    _ <- ZIO.succeed(println("Food arrived!"))
  } yield ()

  // Cleanup action (finalizer)
  private val cleanup: ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.succeed(println(s"Cleaning up kitchen and delivery packages..."))
    _ <- delay(10.millis)
    _ <- ZIO.succeed(println("All clean"))
  } yield ()

  // The final run logic combining all concepts:
  // Forking, Joining, Dependencies (await/zip), Interruption, and Finalizers
  override def run: ZIO[Any, Any, Any] = {
    val pastaChain = boilWater() *> boilPasta()
    val sauceChain = prepareIngredient("tomato") &> prepareIngredient("onion") *> makeSauce()
    val dish = pastaChain <&> sauceChain

    for {
      res <- orderFood().raceEither(dish).ensuring(cleanup)
      res1 <- orderFood().race(dish).ensuring(cleanup)
      _ <- zio.Console.printLine(res)
    } yield ()
  }
}
