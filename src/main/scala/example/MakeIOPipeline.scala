package example

import java.util.Random
import cats.effect._
import fs2.Stream
import scala.concurrent.ExecutionContext


object MakeIOPipeline {

  def mkPipeline: IO[Vector[Int]] = {
    mkPipelineStream(Seed).compile.toVector
  }

  private def mkPipelineStream(seed: Long): Stream[IO, Int] = for {
    rnd <- mkRandomGenerator(seed)
    res <- Stream.repeatEval(randomNumberIO(rnd)).take(10)
  } yield res

  private def randomNumberIO(rnd: Random): IO[Int] = IO {
    rnd.nextInt()
  }

  private def mkRandomGenerator(seed: Long): Stream[IO, Random] = {
    Stream.eval(IO { new Random(seed) })
  }

  private implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  private val Seed: Long = 100

}
