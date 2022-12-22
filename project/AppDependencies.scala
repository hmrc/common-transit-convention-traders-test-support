import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28"       % "7.12.0",
    "com.typesafe.play" %% "play-iteratees"                  % "2.6.1",
    "com.typesafe.play" %% "play-iteratees-reactive-streams" % "2.6.1",
    "org.scalacheck"    %% "scalacheck"                      % "1.15.4",
    "org.typelevel"     %% "cats-core"                       % "2.7.0",
    "io.chrisdavenport" %% "cats-scalacheck"                 % "0.3.0"
  )

  val test = Seq(
    "org.mockito"             % "mockito-core"         % "4.2.0",
    "org.scalatest"          %% "scalatest"            % "3.2.10",
    "com.typesafe.play"      %% "play-test"            % current,
    "org.scalatestplus.play" %% "scalatestplus-play"   % "4.0.3",
    "org.scalatestplus"      %% "mockito-3-2"          % "3.1.2.0",
    "org.scalacheck"         %% "scalacheck"           % "1.15.4",
    "com.github.tomakehurst"  % "wiremock-standalone"  % "2.27.2",
    "org.typelevel"          %% "cats-laws"            % "2.7.0",
    "org.typelevel"          %% "discipline-core"      % "1.4.0",
    "org.typelevel"          %% "discipline-scalatest" % "2.1.5",
    "com.vladsch.flexmark"    % "flexmark-all"         % "0.62.2"
  ).map(_ % "test, it")
}
