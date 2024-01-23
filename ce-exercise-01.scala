//> using toolkit org.typelevel:latest

class MyIO[A](val unsafeRun: () => A):
  def map[B](f: A => B): MyIO[B] =
    MyIO(() => f(unsafeRun()))

  def flatMap[B](f: A => MyIO[B]): MyIO[B] =
    MyIO { () => f(unsafeRun()).unsafeRun() }


def currentTime(): MyIO[Long] = MyIO { () => System.currentTimeMillis() }

def measure[A](computation: MyIO[A]): MyIO[Long] = {
  for {
    s <- currentTime()
    _ <- computation
    e <- currentTime()
  } yield e - s
}

@main
def main =
  val program = for {
    n <- MyIO { () => scala.io.StdIn.readLine("Enter your name: ")  }
    elapTime <- measure(MyIO { () => Thread.sleep(1000) })
    _ <- MyIO { () => println(s"It took ${elapTime} mills to run the MyIO for $n") }
  } yield ()
  program.unsafeRun()
  
