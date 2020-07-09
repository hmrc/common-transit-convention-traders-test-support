import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(

    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "1.13.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "1.13.0" % Test classifier "tests",
    "org.scalatest"           %% "scalatest"                % "3.2.0"                 % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "3.1.3"                 % "test, it",
    "org.scalacheck"          %% "scalacheck"               % "1.14.3"                % "test, it",
    "org.mockito"             %  "mockito-all"              % "2.0.2-beta"            % "test, it",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.35.10"               % "test, it",
    "com.github.tomakehurst"  %  "wiremock-standalone"      % "2.27.1"                % "it"
  )

}
