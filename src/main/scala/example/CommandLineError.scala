package example

import com.monovore.decline.Help


final case class CommandLineError(help: Help) extends RuntimeException
