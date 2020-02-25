package example

import cats.syntax.apply._
import com.monovore.decline.{Command, Opts}

trait CommandLine {

  type Args = Arguments

  private val startOpt: Opts[Double] = Opts.argument[Double](metavar = "file").validate("must be positive") { _ > 0.0 }

  private val verbosityOpt: Opts[Int] = Opts.argument[Int](metavar = "verbosity").validate("must be positive") { _ > 0 }

  private val allOpts: Opts[Args] = (startOpt, verbosityOpt).mapN(Arguments.apply)

  val command: Command[Args] = Command(name = "example", header = "example command", helpFlag = true)(allOpts)

}
