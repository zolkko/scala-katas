import sbt._
import Keys._

// lazy val ScalaVersion    = "2.13.1"
lazy val ScalaVersion       = "2.12.8"

lazy val CatsVersion        = "2.0.0"
lazy val CatsEffectVersion  = "2.0.0"
lazy val Fs2Version         = "2.0.1"

lazy val BreezeVersion      = "1.0.0"
lazy val DeclineVersion     = "1.0.0"

lazy val RainierVersion     = "0.3.0"
lazy val RainierCatsVersion = "0.2.3"

lazy val LogbackVersion     = "1.2.3"
lazy val Log4CatsVersion    = "1.0.1"

lazy val ScalaTestVersion   = "3.0.8"
lazy val ScalaCheckVersion  = "1.14.2"

lazy val protobufSettings = Seq(
  PB.targets in Compile := Seq(scalapb.gen() -> (sourceManaged in Compile).value)
)

lazy val root = (project in file(".")).
  enablePlugins(GraalVMNativeImagePlugin).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := ScalaVersion,
      version      := "0.1.0-SNAPSHOT"
    )),
    mainClass in Compile := Some("example.Main"),
    scalacOptions := Seq(
      "-deprecation",
      "-encoding",
      "UTF-8"
    ),
    name := "scala-katas",
    libraryDependencies ++= Seq(
      "org.typelevel"      %% "cats-effect"     % CatsEffectVersion,
      "co.fs2"             %% "fs2-core"        % Fs2Version,
      "com.monovore"       %% "decline"         % DeclineVersion,

      "ch.qos.logback"     %  "logback-classic" % LogbackVersion,
      "io.chrisdavenport"  %% "log4cats-core"   % Log4CatsVersion,
      "io.chrisdavenport"  %% "log4cats-slf4j"  % Log4CatsVersion,

      "com.stripe"         %% "rainier-core"    % RainierVersion,
      "com.stripe"         %% "rainier-cats"    % RainierCatsVersion,

      "org.scalatest"      %% "scalatest"       % ScalaTestVersion  % Test,
      "org.scalacheck"     %% "scalacheck"      % ScalaCheckVersion % Test,
    ),
  )
