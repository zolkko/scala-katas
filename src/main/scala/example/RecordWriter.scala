package example

import java.io.OutputStream
import java.nio.channels.{Channels, FileChannel}
import java.nio.file.{Path, StandardOpenOption}

import cats.effect.{Concurrent, Resource, Sync, Timer}
import cats.syntax.flatMap._
import fs2.{Pipe, Stream}

import scala.concurrent.duration._


/** Shows why multithreading is hard */
object RecordWriter {

  def writeChunks[F[_]: Sync: Concurrent: Timer](output: Path): Pipe[F, Array[Byte], Unit] = { stream =>
    val syncF = Sync[F]
    val resource = Resource.fromAutoCloseable(syncF.delay { mkChannelStream(output) })
    Stream.resource(resource).flatMap { writer =>
      stream
        .evalMap { record => syncF.delay(writer.write(record)) }
        .debounce(DebounceTime)
        .evalMap { _ => syncF.delay(writer.flush()) } ++ Stream.eval(syncF.delay(writer.flush()))
    }
  }

  def writeChunksParallel[F[_]: Sync: Concurrent: Timer](output: Path): Pipe[F, Array[Byte], Unit] = { stream =>
    val syncF = Sync[F]
    val resource = Resource.fromAutoCloseable(syncF.delay { mkChannelStream(output) })
    Stream.resource(resource).flatMap { writer =>
      val debounceStream = Stream.awakeEvery[F](DebounceTime).evalMap { _ => syncF.delay(writer.flush()) }
      stream
        .evalMap { record => syncF.delay(writer.write(record)) }
        .mergeHaltL(debounceStream)
    }
  }

  def flushEveryNew[F[_]: Sync: Concurrent: Timer](output: Path): Pipe[F, Array[Byte], Unit] = { stream =>
    val syncF = Sync[F]
    val resource = Resource.fromAutoCloseable(syncF.delay { mkChannelStream(output) })
    Stream.resource(resource).flatMap { writer =>
      stream
        .evalScan(0.asInstanceOf[Byte]) { case (idx, chunk) =>
          if (chunk.head == idx) {
            syncF.delay {
              writer.write(chunk)
              writer.flush()
              chunk.head
            }
          } else {
            syncF.delay {
              writer.write(chunk)
              chunk.head
            }
          }
        }
        .map(_ => ())
    }
  }

  private def mkChannelStream(output: Path): OutputStream = {
    val channel = FileChannel.open(output, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
    Channels.newOutputStream(channel)
  }

  private val DebounceTime: FiniteDuration = 100.millis

}
