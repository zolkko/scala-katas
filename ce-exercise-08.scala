//> using toolkit org.typelevel:default

import java.nio.ByteBuffer;
import java.nio.file.{Path, Paths, StandardOpenOption}
import java.nio.channels.FileChannel;

import cats.syntax.parallel.*
import cats.syntax.traverse.*

import cats.effect.{IO, IOApp, Resource}
import cats.effect.std.{Console, CountDownLatch}

object FileServer:
  val chunks = List("1", "22", "333", "4444")
  def getNumChunks: IO[Int] = IO.pure(chunks.length)
  def getFileChunk(n: Int): IO[String] =
    if n < chunks.length then IO.pure(chunks(n))
    else IO.raiseError(RuntimeException(s"$n chunk does not exists"))

def writeToFile(path: Path, contents: String): IO[Unit] =
  val writerRes = Resource.make(
    IO(
      FileChannel.open(
        path,
        StandardOpenOption.WRITE,
        StandardOpenOption.CREATE
      )
    )
  )(channel => IO(channel.close))
  writerRes.use { writer =>
    val bytes = ByteBuffer.wrap(contents.getBytes())
    IO(writer.write(bytes))
  }

def makeReader(input: Path) =
  Resource.make(IO(FileChannel.open(input, StandardOpenOption.READ)))(rdr =>
    IO.delay(rdr.close())
  )

def makeWriter(output: Path) =
  Resource.make(
    IO(
      FileChannel.open(
        output,
        StandardOpenOption.WRITE,
        StandardOpenOption.APPEND,
        StandardOpenOption.CREATE
      )
    )
  )(wrt => IO(wrt.close()))

def appendToFile(input: Path, output: Path): IO[Unit] =
  val res = for
    reader <- makeReader(input)
    writer <- makeWriter(output)
  yield (reader, writer)

  res.use { case (r, w) =>
    val BUFF_SIZE: Int = 2 // 1024
    IO.delay {
      val buffer = ByteBuffer.allocate(BUFF_SIZE)
      while r.read(buffer) != -1 do
        buffer.flip()
        w.write(buffer)
        buffer.clear()
    }
  }

def tmpFile(dir: Path, i: Int): Path = dir.resolve(s"chunk_$i.tmp")

def downloadFile(tempDir: Path, output: Path): IO[Unit] =
  for
    n <- FileServer.getNumChunks
    latch <- CountDownLatch[IO](n)
    _ <- List.range(0, n).parTraverse { i =>
      for
        chunk <- FileServer.getFileChunk(i)
        _ <- writeToFile(tmpFile(tempDir, i), chunk)
        _ <- latch.release
      yield ()
    }
    _ <- latch.await
    _ <- List.range(0, n).traverse { i =>
      appendToFile(tmpFile(tempDir, i), output)
    }
  yield ()

object DownloaderApp extends IOApp.Simple:
  def run: IO[Unit] =
    downloadFile(Paths.get("/tmp"), Paths.get("output.txt")).void
      .handleErrorWith { case err =>
        Console[IO].println(
          s"The program failed with an exception. Reason: ${err.getMessage}"
        )
      }
