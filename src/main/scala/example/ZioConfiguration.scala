package example

import cats.syntax.apply._
import com.monovore.decline.{Command, Opts}
import zio.{Has, Layer, RIO, Task, ZIO, ZLayer}


case class ApiConfig(endpoint: String, port: Int)
case class DbConfig(url: String, user: String, password: String)
case class Config(api: ApiConfig, dbConfig: DbConfig)

object configuration {

  type Configuration = Has[Configuration.Service]

  object Configuration {
    trait Service {
      def load(args: List[String]): Task[Config]
    }

    final class ZioCmd extends Configuration.Service {
      private val endpointOpt: Opts[String] = Opts.argument[String](metavar = "endpoint")
        .withDefault("localhost")
        .map(_.trim)
        .validate("endpoint must not be empty"){ _.nonEmpty }

      private val portOpt: Opts[Int] = Opts.argument[Int](metavar = "port")
        .withDefault(8080)
        .validate("port must be positive") { _ > 0 }

      private val urlOpt: Opts[String] = Opts.argument[String](metavar = "url")
        .withDefault("localhost")
        .map(_.trim)
        .validate("url must not be empty") { _.nonEmpty }

      private val userOpt: Opts[String] = Opts.argument[String](metavar = "user")
        .withDefault("username")
        .map(_.trim)
        .validate("username must not be empty") { _.nonEmpty }

      private val passwordOpt: Opts[String] = Opts.argument[String](metavar = "password")
        .map(_.trim)
        .withDefault("")

      private val apiConfig: Opts[ApiConfig] = (endpointOpt, portOpt).mapN(ApiConfig.apply)

      private val dbConfig: Opts[DbConfig] = (urlOpt, userOpt, passwordOpt).mapN(DbConfig.apply)

      private val configOpt: Opts[Config] = (apiConfig, dbConfig).mapN(Config.apply)

      private val command: Command[Config] = Command(name = "zio-example", header = "ZIO example command", helpFlag = true)(configOpt)

      def load(args: List[String]): Task[Config] = ZIO.fromEither(command.parse(args)).mapError(CommandLineError)
    }

    val confLayer: Layer[Nothing, Configuration] = ZLayer.succeed(new ZioCmd)
  }

  def configFromCmd(args: List[String]): RIO[Configuration, Config] = RIO.accessM(_.get.load(args))
}
