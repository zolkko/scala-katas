//> using toolkit org.typelevel:default
/*
       \ . X 
     X       /
    -         .
    X   [R]   X
    .         .
     /       \
       . X .
 */

import scala.concurrent.duration.*

import cats.*
import cats.syntax.all.*
import cats.syntax.parallel.*

import cats.effect.{IO, IOApp}
import cats.effect.std.{Random, Semaphore, Console}


object DiningPhilosophers extends IOApp.Simple:

    type Chopstick = Semaphore[IO]

    def eating(name: String, first: Chopstick, second: Chopstick)(using Random[IO]): IO[Unit] = for
        r <- Random[IO].betweenInt(1, 5)
        _ <- first.acquire
        _ <- second.acquire
        _ <- Console[IO].println(s"[$name] eating for $r seconds")
        _ <- IO.sleep(r.seconds)
        _ <- Console[IO].println(s"[$name] finished eating")
        _ <- second.release
        _ <- first.release
    yield ()

    def thinking(name: String)(using Random[IO]): IO[Unit] = for
        r <- Random[IO].betweenInt(1, 5)
        _ <- Console[IO].println(s"[$name] thinking for $r seconds")
        _ <- IO.sleep(r.seconds)
        _ <- Console[IO].println(s"[$name] finished thinking")
    yield ()

    def philosopher(name: String, first: Chopstick, second: Chopstick)(using Random[IO]): IO[Unit] = for
        _ <- eating(name, first, second)
        _ <- thinking(name)
        _ <- philosopher(name, first, second)
    yield ()

    def run: IO[Unit] = for
        _           <- Console[IO].println("Starting the program...")
        random      <- Random.scalaUtilRandom[IO]
        chopsticks  <- (1 to 5).toList.traverse(_ => Semaphore[IO](1))
        // dead locks...
        _ <- (0 to 4).toList.parTraverse { i =>
            val first = chopsticks(i % 2)
            val second = chopsticks((i + 1) % 2) // (i + 1) % 5)
            philosopher(s"$i", first, second)(using random)
        }.void
        // no dead lock
        /*
        _           <- (
            philosopher("1", chopsticks(0), chopsticks(1))(using random),
            philosopher("2", chopsticks(1), chopsticks(2))(using random),
            philosopher("3", chopsticks(2), chopsticks(3))(using random),
            philosopher("4", chopsticks(3), chopsticks(4))(using random),
            philosopher("5", chopsticks(0), chopsticks(4))(using random)
        ).parTupled
        */
    yield ()
