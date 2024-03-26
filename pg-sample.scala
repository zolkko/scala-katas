//> using toolkit org.typelevel:default
//> using dep org.tpolecat:skunk-core_3:0.6.3

import cats.effect.{IO, IOApp, Resource}
import cats.effect.std.Console
import cats.syntax.all.*
import skunk.*
import skunk.implicits.*
import skunk.codec.all.*
import natchez.Trace.Implicits.noop


object Main extends IOApp.Simple:

  case class Fields(txt: String, varCh: String)

  val mkSession: Resource[IO, Session[IO]] = Session.single(
      host     = "localhost",
      port     = 5432,
      user     = "jimmy",
      database = "world",
      password = Some("banana")
  )

  val nameQuery = sql"SELECT 'testing', 'demo'::varchar(10)"

  val fieldsDecoder: Decoder[Fields] = (text *: varchar(10)).to[Fields]

  val frag1: Fragment[Void] = sql"SELECT * FROM product as p "
  val frag2: Fragment[Void] = sql"WHERE p.price > 2"

  val frag3 = (frag1 *: frag2).sql
  println(s"Resulting FRAG: $frag3")

  def run: IO[Unit] =
    for
      _ <- Console[IO].println("Start The Program")
      r <- mkSession.use { session =>
        Console[IO].println("Before Executing Query") *>
        session.unique(nameQuery.query(text ~ varchar(10)).map { case t ~ v =>
          Fields(t, v)
        })
      }
      _ <- Console[IO].println(s"Value from the Database: $r")
      r <- mkSession.use { session =>
        Console[IO].println("Next Query") *>
        session.execute(nameQuery.query(fieldsDecoder))
      }
      _ <- Console[IO].println(s"Value: $r")
      _ <- Console[IO].println("End The Program")
    yield ()
