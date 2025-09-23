import play.sbt.routes.RoutesKeys
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings

import scala.collection.immutable.Seq

val appName = "common-transit-convention-traders-test-support"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.6.4"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(inConfig(Test)(ScalafmtPlugin.scalafmtConfigSettings))
  .settings(inConfig(Test)(testSettings)*)
  .settings(Compile / unmanagedResourceDirectories += baseDirectory.value / "resources")
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    ThisBuild / scalafmtOnCompile := true,
    ThisBuild / useSuperShell := false
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(ScoverageSettings())
  .settings(inThisBuild(buildSettings))
  .settings(PlayKeys.playDefaultPort := 9497)
  .settings(scoverageSettings*)
  .settings(
    RoutesKeys.routesImport ++= Seq(
      "models._",
      "models.Bindings._"
    )
  )
  .settings(scalacSettings)

lazy val scalacSettings = Def.settings(
  // Disable dead code warning as it is triggered by Mockito any()
  Test / scalacOptions ~= {
    opts =>
      opts.filterNot(Set("-Wdead-code"))
  },
  scalacOptions += "-Wconf:src=routes/.*:s",
  scalacOptions += "-Wconf:msg=Flag.*repeatedly:s",
  scalacOptions := scalacOptions.value.map {
    case "-Ykind-projector" => "-Xkind-projector"
    case option             => option
  }
)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())

lazy val scoverageSettings =
  Seq(
    ScoverageKeys.coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo*;.*\.Routes;.*\.RoutesPrefix;.*\.Reverse[^.]*;testonly;config.*""",
    ScoverageKeys.coverageMinimumStmtTotal := 90.00,
    ScoverageKeys.coverageExcludedFiles := "<empty>;.*javascript.*;.*Routes.*;",
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    Test / parallelExecution := false
  )

// Settings for the whole build
lazy val buildSettings = Def.settings(
  scalafmtOnCompile := true
)

lazy val testSettings = Seq(
  Test / fork := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf",
    "-Djdk.xml.maxOccurLimit=10000"
  ),
  unmanagedResourceDirectories := Seq(
    baseDirectory.value / "test" / "resources"
  )
)
