//> using toolkit org.typelevel:default

import cats.effect.{IO, IOApp}


def sequenceTakeLast[A, B](ioa: IO[A], iob: IO[B]): IO[B] =
  ioa.flatMap(_ => iob)

def sequenceTakeFirst[A, B](ioa: IO[A], iob: IO[B]): IO[A] =
  ioa.flatMap(a => iob.map(_ => a))

def forever[A](io: IO[A]): IO[A] =
  io.flatMap(_ => forever(io))

def convert[A, B](ioa: IO[A], value: B): IO[B] = ioa.map(_ => value)

def asUnit[A](ioa: IO[A]): IO[Unit] = ioa.map(_ => ())

def sumIO(n: Int): IO[Int] =
  if n <= 0 then IO.pure(0)
  else sumIO(n - 1).map(_ + n)

def  fibonacci(n: Int): IO[BigInt] =
  if n <= 0 then IO.pure(BigInt(0))
  else if n == 1 || n == 2 then IO.pure(BigInt(1))
  else for
    a <- fibonacci(n - 1)
    b <- fibonacci(n - 2)
  yield a + b

object Main extends IOApp.Simple:

  def run: IO[Unit] =
    val subProgram = for
      x <- fibonacci(9)
      _ <- IO { println(s"fibo $x") }
      _ <- sequenceTakeLast(IO.pure(1), IO.pure("123")).map(println)
      _ <- sequenceTakeFirst(IO.pure(1), IO.pure("321")).map(println)
      _ <- IO { Thread.sleep(1000) }
    yield ()
    forever(subProgram)
