import cats.effect.{ExitCode, IO, IOApp}
import scala.concurrent.duration._
import cats.syntax.all._

object PastaApp extends IOApp {

  // Helper to mimic ZIO's delay
  def delay(duration: Duration): IO[Unit] = IO.sleep(duration)

  // --- Effects (Tasks) ---

  def boilWater(): IO[Unit] = for {
    _ <- IO.println("Water put on stove...")
    _ <- delay(100.millis)
    _ <- IO.println("Water boiled!")
  } yield ()

  def boilPasta(): IO[Unit] = for {
    _ <- IO.println("Put pasta in boiling water...")
    _ <- delay(1.second)
    _ <- IO.println("Pasta ready!")
  } yield ()

  def prepareIngredient(ingredient: String): IO[Unit] = for {
    _ <- IO.println(s"Preparing $ingredient...")
    _ <- delay(300.millis)
    _ <- IO.println(s"$ingredient ready")
  } yield ()

  def makeSauce(): IO[Unit] = for {
    _ <- IO.println("Preparing sauce...")
    _ <- delay(1.second)
    _ <- IO.println("Sauce is ready")
  } yield ()

  def orderFood(): IO[Unit] = for {
    _ <- IO.println("Ordering some food...")
    _ <- delay(1.second)
    _ <- IO.println("Food arrived!")
  } yield ()

  val cleanup: IO[Unit] = for {
    _ <- IO.println("Cleaning up kitchen and delivery packages...")
    _ <- delay(10.millis)
    _ <- IO.println("All clean")
  } yield ()

  // --- Main Program ---

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      // 1. Fork the water boiling fiber (Independent)
      waterFiber <- boilWater().start

      // 2. Fork the pasta fiber.
      // Logic: Wait for waterFiber to finish (join), THEN boil pasta.
      // ZIO: waterFiber.await.zip(boilPasta()).fork
      pastaFiber <- (waterFiber.join >> boilPasta()).start

      // 3. Fork tomato preparation (Independent)
      tomatoFiber <- prepareIngredient("tomato").start

      // 4. Fork onion preparation.
      // Logic: Wait for tomato to finish, THEN prepare onion.
      onionFiber <- (tomatoFiber.join >> prepareIngredient("onion")).start

      // 5. Fork sauce preparation.
      // Logic: Wait for onion to finish, THEN make sauce.
      sauceFiber <- (onionFiber.join >> makeSauce()).start

      // 6. Interrupt the pasta and sauce chains.
      // ZIO: pastaFiber.zip(sauceFiber).interrupt
      // We cancel both fibers. This stops the "waiting" or the subsequent task.
      // Note: waterFiber and tomatoFiber are independent, so they continue running.
      _ <- (pastaFiber.cancel, sauceFiber.cancel).parTupled

      // 7. Order food instead.
      orderFiber <- orderFood().start

      // 8. Join the order fiber and ensure cleanup runs afterwards.
      // ZIO: orderFiber.join.ensuring(cleanup)
      _ <- orderFiber.join.guarantee(cleanup)

    } yield ExitCode.Success
  }
}

import cats.effect.{ExitCode, IO, IOApp}
import scala.concurrent.duration._
import cats.syntax.all._

object PastaApp1 extends IOApp {

  def delay(duration: Duration): IO[Unit] = IO.sleep(duration)

  // --- Individual Tasks ---

  def boilWater(): IO[Unit] =
    IO.println("Water put on stove...") >> delay(100.millis) >> IO.println("Water boiled!")

  def boilPasta(): IO[Unit] =
    IO.println("Put pasta in boiling water...") >> delay(1.second) >> IO.println("Pasta ready!")

  def prepareIngredient(ingredient: String): IO[Unit] =
    IO.println(s"Preparing $ingredient...") >> delay(300.millis) >> IO.println(s"$ingredient ready")

  def makeSauce(): IO[Unit] =
    IO.println("Preparing sauce...") >> delay(1.second) >> IO.println("Sauce is ready")

  def orderFood(): IO[String] = for {
    _ <- IO.println("Ordering some food...")
    _ <- delay(1.second) // Ordering takes 1 second total
    _ <- IO.println("Food arrived!")
  } yield "Delivery Pizza"

  val cleanup: IO[Unit] = for {
    _ <- IO.println("Cleaning up kitchen and delivery packages...")
    _ <- delay(10.millis)
    _ <- IO.println("All clean")
  } yield ()

  // --- Composite Tasks ---

  // The full cooking process
  val makeDinner: IO[String] = {
    val pastaChain = boilWater() >> boilPasta()
    val sauceChain = prepareIngredient("tomato") >> prepareIngredient("onion") >> makeSauce()

    // Run both chains in parallel; wait for both to finish
    (pastaChain, sauceChain).parTupled.as("Homemade Pasta")
  }

  // --- Main Program ---

  override def run(args: List[String]): IO[ExitCode] = {
    // We race making dinner vs ordering food.
    // Cooking takes approx 1.6s (Sauce chain is the bottleneck).
    // Ordering takes 1.0s.
    // Therefore, Ordering should win, and Cooking should be cancelled.
    IO.race(makeDinner, orderFood())
      .flatMap {
        case Left(homeMade) => IO.println(s"Winner: We are eating $homeMade")
        case Right(delivery) => IO.println(s"Winner: We are eating $delivery")
      }
      .guarantee(cleanup)
      .as(ExitCode.Success)
  }
}
