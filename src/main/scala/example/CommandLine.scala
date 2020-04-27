package example

import cats.syntax.apply._
import com.monovore.decline.{Command, Opts}

trait CommandLine {

  type Args = Arguments

  private val delayOpt: Opts[Double] = Opts.argument[Double](metavar = "delay").validate("must be positive") { _ > 0.0 }

  private val verbosityOpt: Opts[Int] = Opts.argument[Int](metavar = "verbosity").validate("must be positive") { _ > 0 }

  private val allOpts: Opts[Args] = (delayOpt, verbosityOpt).mapN(Arguments.apply)

  val command: Command[Args] = Command(name = "example", header = "example command", helpFlag = true)(allOpts)

}
