package example

import cats.effect.IO
import org.scalatest._
import fs2.{Stream, Pure}
import scala.io.Codec

final class CountLinesSpec extends FlatSpec with Matchers {

  "countLines" should "count trailing lines" in {
    val bytes = Codec.toUTF8("line1\nline2\n\n\nline5\nline6")

    val countIo = Stream.emits(bytes)
      .chunkLimit(3)
      .through(countLines[IO])
      .compile.fold(0L) { _ + _ }

    countIo.unsafeRunSync() shouldBe 6
  }

  it should "count non trailing lines" in {
    val bytes = Codec.toUTF8("line1\nline2\n\n\nline5\n")

    val countIo = Stream.emits(bytes)
      .chunkLimit(3)
      .through(countLines[IO])
      .compile.fold(0L) { _ + _ }

    countIo.unsafeRunSync() shouldBe 5
  }

  it should "count empty stream" in {
    val countIo = Stream.emits(Array.empty[Byte])
      .chunkLimit(3)
      .through(countLines[IO])
      .compile.fold(0L) { _ + _ }

    countIo.unsafeRunSync() shouldBe 0
  }

  it should "count one line" in {
    val countIo = Stream.emits(Codec.toUTF8("one line"))
      .chunkLimit(3)
      .through(countLines[IO])
      .compile.fold(0L) { _ + _ }

    countIo.unsafeRunSync() shouldBe 1
  }

}
