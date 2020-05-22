package example

import zio.{App, ZIO}
import zio.console._


object ZioMain extends App {

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    theApp.fold(_ => 1, _ => 0)
  }

  private val theApp: ZIO[Console, Throwable, Unit] = for {
    _ <- putStrLn("ZIO test")
  } yield ()

}
