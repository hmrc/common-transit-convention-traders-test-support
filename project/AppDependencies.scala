import sbt._

object AppDependencies {

  private val bootstrapVersion  = "8.4.0"
  private val scalaCheckVersion = "1.17.0"

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapVersion,
    "org.typelevel"     %% "cats-core"                 % "2.10.0",
    "io.chrisdavenport" %% "cats-scalacheck"           % "0.3.2"
  )

  val test = Seq(
    "org.mockito"        % "mockito-core"           % "5.8.0",
    "org.scalatestplus" %% "mockito-3-2"            % "3.1.2.0",
    "org.scalacheck"    %% "scalacheck"             % scalaCheckVersion,
    "org.typelevel"     %% "cats-laws"              % "2.10.0",
    "org.typelevel"     %% "discipline-core"        % "1.5.1",
    "org.typelevel"     %% "discipline-scalatest"   % "2.2.0",
    "uk.gov.hmrc"       %% "bootstrap-test-play-30" % bootstrapVersion
  ).map(_ % Test)
}
