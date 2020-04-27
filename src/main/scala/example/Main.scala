package example

import cats.syntax.apply._
import cats.syntax.functor._
import cats.syntax.either._
import cats.effect._
import cats.effect.concurrent.Ref


object Main extends IOApp with CommandLine {

  type Actor[I, O] = I => IO[O]

  def mkActor: IO[Actor[Int, Int]] = for {
    counter <- Ref[IO].of(0)
    actor = (n: Int) => counter.modify { x =>
      (x + n, x)
    }
  } yield actor

  def putStrLn(value: String): IO[Unit] = IO { println(value) }

  def parseArgs(args: List[String]): IO[Main.Args] = IO.fromEither {
    command.parse(args).leftMap(CommandLineError)
  }

  def program(args: List[String]): IO[Unit] = for {
    lst    <- MakeIOPipeline.mkPipeline
    _      <- putStrLn(s"List = $lst")
    params <- parseArgs(args)
    _      <- putStrLn(s"arguments: $params")
    act    <- mkActor
    _      <- act(1)
    _      <- act(1)
    z      <- act(0)
    _      <- putStrLn(s"result: $z")
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
