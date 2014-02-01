import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "qubuto2"
  val appVersion      = "0.1-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "play-jongo" %% "play-jongo" % "1.0-SNAPSHOT",
    javaCore,
    javaJdbc,
    javaEbean
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
    resolvers += "Local Play Repository" at "file://home/remy/docs/apps/play-2.1.0/repository/local",
    requireJs += "commons.js", // is this one really usefull ?
    requireJs += "projects.js", // is this one really usefull ?
    requireJs += "conversations.js"
  )

}
