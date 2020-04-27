import cats.effect._
import fs2.{Chunk, Pipe, Pull, Stream}
import scala.annotation.tailrec

package object example {

  /**
    * Count number of lines in the input.
    */
  def countLines[F[_]: Sync]: Pipe[F, Chunk[Byte], Long] = {
    @tailrec
    def newlines(buffer: Chunk[Byte], n: Int): (Chunk[Byte], Int) = {
      buffer.indexWhere(_ == '\n') match {
        case Some(i) =>
          val (_, tail) = buffer.splitAt(i + 1)
          newlines(tail, n + 1)
        case None => (buffer, n)
      }
    }

    def doPull(buffer: Chunk[Byte], s: Stream[F, Chunk[Byte]]): Pull[F, Long, Unit] = {
      s.pull.uncons1.flatMap {
        case Some((byteChunks, tail)) =>
          val (rest, cnt) = newlines(byteChunks, 0)
          Pull.output1(cnt.toLong) >> doPull(rest, tail)

        case None if !buffer.isEmpty =>
          // the last line in the file does not ends with newline symbol
          fs2.Pull.output1(1)

        case None =>
          // no more data available
          fs2.Pull.done
      }
    }

    (in: Stream[F, Chunk[Byte]]) => doPull(Chunk.empty, in).stream
  }


  import cats.effect._
  import cats.implicits._

  def printValue[F[_]: Sync](char: Char, count: Int): F[Unit] = Sync[F].delay { println(s"$char - $count") }

  def printValue[F[_]: Sync](x: (Char, Int)): F[Unit] = printValue(x._1, x._2)

  def processText(text: String): List[(Char, Int)] = {
    text.toSeq.collect { case chr if chr.isLetter => chr.toLower }
      .groupBy(identity).map { case (k, v) => k -> v.length }.toList.sortBy(_._1)
  }

  def processTextAndPrint[F[_]: Sync](text: String): F[Unit] = {
    processText(text).traverse(printValue[F]) >> Sync[F].pure(())
  }

  def processTextAndPrint2(text: String): Unit = {
    text.toSeq.filter(_.isLetter).map(_.toLower)
      .groupBy(identity)
      .toList.map { case (k, v) => k -> v.length }.sortBy(_._1).foreach { case (k, v) => println(s"$k - $v") }
  }

}
