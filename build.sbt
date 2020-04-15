import xerial.sbt.Sonatype._

ThisBuild / name := "mtg4s"
ThisBuild / scalaVersion := "2.13.1"
ThisBuild / organization := "com.gaborpihaj"
ThisBuild / dynverSonatypeSnapshots := true
ThisBuild / scalafixDependencies += "com.nequissimus" %% "sort-imports" % "0.3.2"

ThisBuild / publishTo := sonatypePublishToBundle.value

val catsVersion = "2.1.1"
val catsEffectVersion = "2.1.2"
val enumeratumVersion = "1.5.13"
val enumeratumCirceVersion = "1.5.23"
val circeVersion = "0.12.3"
val shapelessVersion = "2.3.3"
val monocleVersion = "2.0.3"
val kantanCsvVersion = "0.6.0"

val scalatestVersion = "3.1.1"
val scalatestScalacheckVersion = "3.1.1.1"
val scalaCheckVersion = "1.14.3"

lazy val publishSettings = List(
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  publishMavenStyle := true,
  sonatypeProjectHosting := Some(GitHubHosting("voidcontext", "mtg4s", "gabor.pihaj@gmail.com"))
)

lazy val defaultSettings = Seq(
  addCompilerPlugin(scalafixSemanticdb),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)
)

lazy val core = (project in file("core"))
  .settings(
    publishSettings,
    defaultSettings,
    libraryDependencies ++= Seq(
      "com.chuusai"       %% "shapeless"       % shapelessVersion,
      "org.scalatest"     %% "scalatest"       % scalatestVersion % Test,
      "org.scalatestplus" %% "scalacheck-1-14" % scalatestScalacheckVersion % Test,
      "org.scalacheck"    %% "scalacheck"      % scalaCheckVersion % Test
    )
  )

lazy val inventory = (project in file("inventory"))
  .settings(
    publishSettings,
    defaultSettings,
    libraryDependencies ++= Seq(
      "org.typelevel"              %% "cats-core"          % catsVersion,
      "org.typelevel"              %% "cats-effect"        % catsEffectVersion,
      "com.nrinaudo"               %% "kantan.csv"         % kantanCsvVersion,
      "com.github.julien-truffaut" %% "monocle-core"       % monocleVersion,
      "org.scalatest"              %% "scalatest"          % scalatestVersion % Test,
      "org.scalatestplus"          %% "scalacheck-1-14"    % scalatestScalacheckVersion % Test,
      "org.scalacheck"             %% "scalacheck"         % scalaCheckVersion % Test,
      "io.chrisdavenport"          %% "cats-scalacheck"    % "0.2.0" % Test,
      "com.nrinaudo"               %% "kantan.csv-generic" % kantanCsvVersion % Test,
      "org.typelevel"              %% "claimant"           % "0.1.3"
    )
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val mtgjson = (project in file("mtgjson"))
  .settings(
    publishSettings,
    defaultSettings,
    parallelExecution in Test := false,
    libraryDependencies ++= Seq(
      "org.typelevel"  %% "cats-core"        % catsVersion,
      "org.typelevel"  %% "cats-effect"      % catsEffectVersion,
      "com.beachape"   %% "enumeratum"       % enumeratumVersion,
      "io.circe"       %% "circe-core"       % circeVersion,
      "io.circe"       %% "circe-generic"    % circeVersion,
      "io.circe"       %% "circe-parser"     % circeVersion,
      "com.beachape"   %% "enumeratum-circe" % enumeratumCirceVersion,
      "org.scalatest"  %% "scalatest"        % scalatestVersion % Test,
      "com.gaborpihaj" %% "fetchfile"        % "0.2.0" % Test
    )
  )
  .dependsOn(core)

lazy val root = (project in file("."))
  .settings(
    skip in publish := true
  )
  .aggregate(
    mtgjson,
    core,
    inventory
  )

addCommandAlias("prePush", ";scalafix ;test:scalafix ;scalafmtAll ;scalafmtSbt ;clean ;test")
