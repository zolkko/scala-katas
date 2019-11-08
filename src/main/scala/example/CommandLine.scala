package example

import cats.syntax.apply._
import com.monovore.decline.{Command, Opts}

trait CommandLine {

  type Args = (Double, Double)

  private val startOpt: Opts[Double] = Opts.argument[Double](metavar = "start").validate("must be positive") { _ > 0.0 }

  private val endOpt: Opts[Double] = Opts.argument[Double](metavar = "end").validate("must be positive") { _ > 0.0 }

  private val allOpts: Opts[Args] = (startOpt, endOpt).tupled

  val command: Command[Args] = Command(name = "example", header = "example command", helpFlag = true)(allOpts)

}
