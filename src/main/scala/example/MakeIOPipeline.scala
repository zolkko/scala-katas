package example

import java.util.Random

import cats.effect._
import cats.effect.concurrent.Deferred
import fs2.Stream

import scala.concurrent.ExecutionContext


object MakeIOPipeline {

  def mkPipeline(switch: Deferred[IO, Unit]): IO[Unit] = {
    mkPipelineStream(Seed)
      .interruptWhen(switch.get.attempt)
      .parEvalMapUnordered(MaxConcurrency)(x => IO {
        println(s"item: $x")
        x
      })
      .compile
      .drain
  }

  private def mkPipelineStream(seed: Long): Stream[IO, Int] = for {
    rnd <- mkRandomGenerator(seed)
    res <- Stream.repeatEval(randomNumberIO(rnd))
  } yield res

  private def randomNumberIO(rnd: Random): IO[Int] = IO {
    rnd.nextInt()
  }

  private def mkRandomGenerator(seed: Long): Stream[IO, Random] = {
    Stream.eval(IO { new Random(seed) })
  }

  private implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  private val Seed: Long = 100

  private val MaxConcurrency: Int = 10

}
