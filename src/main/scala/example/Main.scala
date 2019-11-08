package example

import cats.syntax.apply._
import cats.syntax.functor._
import cats.effect._

object Main extends IOApp with CommandLine {

  def putStrLn(value: String): IO[Unit] = IO { println(value) }

  def program(args: List[String]): IO[Unit] = for {
    _ <- putStrLn("example")
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
