import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val catsVersion = "2.6.1"

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-27"       % "5.14.0",
    "com.typesafe.play" %% "play-iteratees"                  % "2.6.1",
    "com.typesafe.play" %% "play-iteratees-reactive-streams" % "2.6.1",
    "org.scalacheck"    %% "scalacheck"                      % "1.15.4",
    "org.typelevel"     %% "cats-core"                       % catsVersion
  )

  val test = Seq(
    "org.mockito"            % "mockito-core"          % "3.3.3",
    "org.scalatest"          %% "scalatest"            % "3.2.9",
    "com.typesafe.play"      %% "play-test"            % current,
    "org.scalatestplus.play" %% "scalatestplus-play"   % "4.0.3",
    "org.scalatestplus"      %% "mockito-3-2"          % "3.1.2.0",
    "org.scalacheck"         %% "scalacheck"           % "1.15.4",
    "com.github.tomakehurst" % "wiremock-standalone"   % "2.27.2",
    "org.typelevel"          %% "cats-laws"            % catsVersion,
    "org.typelevel"          %% "discipline-core"      % "1.1.5",
    "org.typelevel"          %% "discipline-scalatest" % "2.1.5",
    "com.vladsch.flexmark"   % "flexmark-all"          % "0.36.8"
  ).map(_ % "test, it")
}
