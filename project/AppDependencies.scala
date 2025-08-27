import sbt._

object AppDependencies {

  private val bootstrapVersion  = "10.1.0"
  private val scalaCheckVersion = "1.18.1"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapVersion,
    "org.typelevel"     %% "cats-core"                 % "2.13.0",
    "io.chrisdavenport" %% "cats-scalacheck"           % "0.3.2"
  )

  val test: Seq[ModuleID] = Seq(
    "org.mockito"        % "mockito-core"           % "5.14.2",
    "org.scalatestplus" %% "mockito-5-12"           % "3.2.19.0",
    "org.scalacheck"    %% "scalacheck"             % scalaCheckVersion,
    "org.typelevel"     %% "cats-laws"              % "2.13.0",
    "org.typelevel"     %% "discipline-core"        % "1.7.0",
    "org.typelevel"     %% "discipline-scalatest"   % "2.3.0",
    "uk.gov.hmrc"       %% "bootstrap-test-play-30" % bootstrapVersion
  ).map(_ % Test)
}
