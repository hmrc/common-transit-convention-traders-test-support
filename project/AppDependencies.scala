import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val bootstrapVersion  = "7.13.0"
  private val scalaCheckVersion = "1.17.0"

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28" % bootstrapVersion,
    "org.scalacheck"    %% "scalacheck"                % scalaCheckVersion,
    "org.typelevel"     %% "cats-core"                 % "2.9.0",
    "io.chrisdavenport" %% "cats-scalacheck"           % "0.3.2"
  )

  val test = Seq(
    "org.mockito"             % "mockito-core"           % "4.2.0",
    "org.scalatest"          %% "scalatest"              % "3.2.10",
    "com.typesafe.play"      %% "play-test"              % current,
    "org.scalatestplus.play" %% "scalatestplus-play"     % "4.0.3",
    "org.scalatestplus"      %% "mockito-3-2"            % "3.1.2.0",
    "org.scalacheck"         %% "scalacheck"             % scalaCheckVersion,
    "com.github.tomakehurst"  % "wiremock-standalone"    % "2.27.2",
    "org.typelevel"          %% "cats-laws"              % "2.7.0",
    "org.typelevel"          %% "discipline-core"        % "1.4.0",
    "org.typelevel"          %% "discipline-scalatest"   % "2.1.5",
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.62.2",
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapVersion,
  ).map(_ % "test, it")
}
