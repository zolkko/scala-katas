package example

import cats.syntax.apply._
import com.monovore.decline.{Command, Opts, Help}
import zio.{IO, ZIO}


trait ZioCmd {

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

  def parseCommandLine(args: List[String]): IO[Help, Config] = ZIO.fromEither(command.parse(args))

}
