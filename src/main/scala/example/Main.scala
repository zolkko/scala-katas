package example

import cats.syntax.apply._
import cats.syntax.functor._
import cats.effect._
import com.stripe.rainier.sampler.RNG
import com.stripe.rainier.repl.{DensityPlot, plot1D}

object Main extends IOApp with CommandLine with RainierSample {

  private implicit val rng: RNG = RNG.default

  def putStrLn(value: String): IO[Unit] = IO { println(value) }

  def program(args: List[String]): IO[Unit] = for {
    x <- doRegression[IO]((0 until 100).map(x => x -> (x + 1)).toList, 4)
    v <- IO { DensityPlot().plot1D(x.sample().map(_.toDouble)).mkString("\n") }
    _ <- putStrLn(v)
  } yield ()

  def run(args: List[String]): IO[ExitCode] = {
    program(args).as(ExitCode.Success).handleErrorWith { error =>
      val prn = error match {
        case CommandLineError(help) => putStrLn(help.toString)
        case o: Throwable => putStrLn(o.toString)
      }
      prn *> IO.pure(ExitCode.Error)
    }
  }

}
