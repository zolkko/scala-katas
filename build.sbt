import sbt._
import Keys._

lazy val ScalaVersion       = "2.13.2" // 2.12.11

lazy val CatsVersion        = "2.1.1"
lazy val CatsEffectVersion  = "2.1.3"
lazy val Fs2Version         = "2.3.0"

lazy val BreezeVersion      = "1.0.0"
lazy val DeclineVersion     = "1.0.0"

lazy val RainierVersion     = "0.3.0"
lazy val RainierCatsVersion = "0.2.3"

// "com.stripe"         %% "rainier-core"    % RainierVersion
// "com.stripe"         %% "rainier-cats"    % RainierCatsVersion

lazy val LogbackVersion     = "1.2.3"
lazy val Log4CatsVersion    = "1.0.1"

lazy val ScalaTestVersion   = "3.0.8"
lazy val ScalaCheckVersion  = "1.14.2"

lazy val ZIOVersion         = "1.0.0-RC19"
lazy val ZIOInterop         = "2.0.0.0-RC14"

lazy val DoobieVersion      = "0.9.0"

lazy val Http4sVersion      = "0.21.4"


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
      "org.typelevel"      %% "cats-effect"      % CatsEffectVersion,
      "co.fs2"             %% "fs2-core"         % Fs2Version,
      "com.monovore"       %% "decline"          % DeclineVersion,

      "ch.qos.logback"     %  "logback-classic"  % LogbackVersion,
      "io.chrisdavenport"  %% "log4cats-core"    % Log4CatsVersion,
      "io.chrisdavenport"  %% "log4cats-slf4j"   % Log4CatsVersion,

      "dev.zio"            %% "zio"              % ZIOVersion,
      "dev.zio"            %% "zio-interop-cats" % ZIOInterop,

      "org.http4s"         %% "http4s-blaze-server"
                                                 % Http4sVersion,
      "org.http4s"         %% "http4s-circe"     % Http4sVersion,
      "org.http4s"         %% "http4s-dsl"       % Http4sVersion,

      "org.tpolecat"       %% "doobie-core"      % DoobieVersion,
      "org.tpolecat"       %% "doobie-h2"        % DoobieVersion,

      "org.scalatest"      %% "scalatest"        % ScalaTestVersion  % Test,
      "org.scalacheck"     %% "scalacheck"       % ScalaCheckVersion % Test,
    ),
    graalVMNativeImageGraalVersion := Some("20.0.0"),
  )

addCommandAlias("graal", "graalvm-native-image:packageBin")
