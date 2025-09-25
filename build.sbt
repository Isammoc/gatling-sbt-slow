enablePlugins(GatlingPlugin)

scalaVersion := "2.13.16"

scalacOptions := Seq(
  "-encoding", "UTF-8", "-release:8", "-deprecation",
  "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps")

val gatlingVersion = "3.14.4"
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % "test,it"
libraryDependencies += "io.gatling"            % "gatling-test-framework"    % gatlingVersion % "test,it"

// Enterprise Cloud (https://cloud.gatling.io/) configuration reference: https://docs.gatling.io/reference/integrations/build-tools/sbt-plugin/#running-your-simulations-on-gatling-enterprise-cloud

import scala.concurrent.duration._

// Setting for the duration of the slow task (configurable as a FiniteDuration)
val duration = settingKey[FiniteDuration]("Duration for the slow task (configurable via sbt 'set duration := X.seconds')")

duration := 2.minutes // Default: 10 seconds

// Definition of the custom slow task
val slowTask = taskKey[Unit]("A slow SBT task with a visible progress counter")

slowTask := {
  val dur = duration.value
  println(s"Starting slow task for ${dur.toSeconds} seconds...")
  for (i <- 1 to dur.toSeconds.toInt) {
    println(s"Progress: $i / ${dur.toSeconds}")
    Thread.sleep(1000) // 1-second pause per iteration
  }
  println("Slow task completed.")
}

// Bonus: Make enterprisePackage depend on the slow task
// This ensures slowTask runs before enterprisePackage
Gatling / enterprisePackage := ((Gatling / enterprisePackage) dependsOn slowTask).value
