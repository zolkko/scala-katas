//> using toolkit org.typelevel:default

import cats.effect.{IO, IOApp}
import cats.{Parallel, Traverse}
import cats.instances.list.*
import cats.syntax.traverse.*
import cats.syntax.parallel.*


def sequence[A](listOfIOs: List[IO[A]]): IO[List[A]] =
  // def traverse[G[_]: Applicative, A, B](fa: F[A])(f: A => G[B]): G[F[B]]
  listOfIOs.traverse(identity)

def sequence2[F[_]: Traverse, A](listOfIOs: F[IO[A]]): IO[F[A]] =
  Traverse[F].traverse(listOfIOs)(identity)

def parSequence[A](listOfIOs: List[IO[A]]): IO[List[A]] =
  listOfIOs.parTraverse(identity)

def parSequence2[F[_]: Traverse, A](listOfIOs: F[IO[A]]): IO[F[A]] =
  listOfIOs.parTraverse(identity)

object Main extends IOApp.Simple:

  def run: IO[Unit] =
    sequence(List(IO(1), IO(2), IO(3))).map(println)
    >> sequence2(Option(IO(123))).map(println)
    >> parSequence(List(IO(321), IO(123))).map(println)
    >> parSequence2(List(IO(123), IO(321))).map(println).void
