//> using toolkit org.typelevel:default


import cats.effect.{IO, IOApp, Ref}
import cats.syntax.parallel.*
import scala.concurrent.duration.*


object Main extends IOApp.Simple {


    def tickingClockImpure(): IO[Unit] = {
        var ticks: Long = 0l

        def tickingClock: IO[Unit] = for {
            _ <- IO.sleep(1.second)
            _ <- IO {
                val t = System.currentTimeMillis()
                println(s"current time: $t")
            }
            _ <- IO(ticks += 1)
            _ <- tickingClock
        } yield ()

        def printTicks: IO[Unit] = for {
            _ <- IO.sleep(5.seconds)
            _ <- IO(println(s"ticks: $ticks"))
            _ <- printTicks
        } yield ()

        for {
            _ <- (tickingClock, printTicks).parTupled
        } yield ()
    }

    def tickingClockPure(): IO[Unit] = {
        def tickingClock(cnt: Ref[IO, Long]): IO[Unit] = for {
            _ <- IO.sleep(1.second)
            _ <- IO {
                val t = System.currentTimeMillis()
                println(s"current time: $t")
            }
            _ <- cnt.updateAndGet(_ + 1)
            _ <- tickingClock(cnt)
        } yield ()

        def printTicks(cnt: Ref[IO, Long]): IO[Unit] = for {
            _ <- IO.sleep(5.seconds)
            t <- cnt.get
            _ <- IO(println(s"ticks: $t"))
            _ <- printTicks(cnt)
        } yield ()

        for {
            ticks <- Ref[IO].of(0l)
            _ <- (tickingClock(ticks), printTicks(ticks)).parTupled
        } yield ()
    }

    def run: IO[Unit] = tickingClockPure()

}