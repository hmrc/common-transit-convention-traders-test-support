import play.sbt.routes.RoutesKeys
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings

val appName = "common-transit-convention-traders-test-support"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(inConfig(Test)(ScalafmtPlugin.scalafmtConfigSettings))
  .settings(inConfig(Test)(testSettings): _*)
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
  .settings(scoverageSettings: _*)
  .settings(
    RoutesKeys.routesImport ++= Seq(
      "models._"
    )
  )
  .settings(
    // silence all warnings on autogenerated files
    scalacOptions += "-Wconf:src=routes/.*:s"
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())

lazy val scoverageSettings =
  Seq(
    ScoverageKeys.coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo*;.*\.Routes;.*\.RoutesPrefix;.*\.Reverse[^.]*;testonly;config.*""",
    ScoverageKeys.coverageMinimumStmtTotal := 85.00,
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
