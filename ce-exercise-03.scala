//> using toolkit org.typelevel:default

import scala.util.{Success, Failure, Try}
import cats.effect.IO


def option2IO[A](value: Option[A])(ifEmpty: Throwable): IO[A] =
  value.fold(IO.raiseError(ifEmpty))(IO(_))

def try2IO[A](value: Try[A]): IO[A] =
  value match
    case Success(x) => IO(x)
    case Failure(e) => IO.raiseError(e)

def either2IO[A](value: Either[Throwable, A]): IO[A] =
  value.fold(IO.raiseError, IO.delay)

def handleIOError[A](io: IO[A])(handler: Throwable => A): IO[A] = io.handleError(handler)

def handleIOErrorWith[A](io: IO[A])(handler: Throwable => IO[A]): IO[A] = io.handleErrorWith(handler)

@main
def main =
  import cats.effect.unsafe.implicits.global
  val program = for
    _ <- option2IO(Some("test option"))(RuntimeException("error"))
    _ <- try2IO(Try("test try"))
    _ <- either2IO(Right("test either"))
  yield ()
  program.unsafeRunSync()
