import sbt._
import Keys._

object Dependencies {
  val scalaTest = Def.setting { CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 8)) => "org.scalatest" %% "scalatest" % "1.8" % "test"
    case Some((2, 9)) => "org.scalatest" %% "scalatest" % "1.9.2" % "test"
    case _            => "org.scalatest" %% "scalatest" % "2.2.5" % "test"
  }}

  val junit = "junit" % "junit" % "4.11" % "test"
}

//  ###########################################################################

object JvmIoBuild extends Build {
  import Default._
  import Dependencies._

  lazy val util = Project(
    "util"
  , file("util")
  , settings = scalaSettings ++ Seq(
      name    := "jvm-util"
    , version := "0.0.1"
    , initialCommands := "import io.jvm._"
    , libraryDependencies ++= Seq(
        scalaTest.value
      , junit
      )
    )
  )

  lazy val jsad = Project(
    "jsad"
  , file("jsad")
  , settings = javaSettings ++ Seq(
      name    := "jvm-jsad"
    , version := "0.1.0"
    , initialCommands := "import io.jvm.jsad._"
    )
  )

  lazy val xml = Project(
    "xml"
  , file("xml")
  , settings = javaSettings ++ Seq(
      name    := "jvm-xml"
    , version := "0.0.1"
    , initialCommands := "import io.jvm._"
    )
  )

/*
  lazy val json = Project(
    "json"
  , file("json")
  , settings = Seq(
      name    := "jvm-json"
    , version := "0.0.1"
    , initialCommands := "import io.jvm._"
    )
  )
*/  
}

//  ---------------------------------------------------------------------------

object Repositories {
  val ElementNexus     = "Element Nexus"     at "http://repo.element.hr/nexus/content/groups/public/"
  val ElementReleases  = "Element Releases"  at "http://repo.element.hr/nexus/content/repositories/releases/"
  val ElementSnapshots = "Element Snapshots" at "http://repo.element.hr/nexus/content/repositories/snapshots/"
}

//  ---------------------------------------------------------------------------

object Resolvers {
  import Repositories._

  lazy val settings = Seq(
    resolvers := Seq(ElementNexus)
  , externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)
  )
}

//  ---------------------------------------------------------------------------

object Publishing {
  import Repositories._

  lazy val settings = Seq(
    publishTo := Some(if (version.value endsWith "-SNAPSHOT") ElementSnapshots else ElementReleases)
  , credentials ++= {
      val creds = Path.userHome / ".config" / "jvm.io" / "nexus.config"
      if (creds.exists) Some(Credentials(creds)) else None
    }.toSeq 
  , publishArtifact in (Compile, packageDoc) := false
  )
}

//  ---------------------------------------------------------------------------

object Default {
  //Eclipse plugin
  import com.typesafe.sbteclipse.plugin.EclipsePlugin.{ projectSettings => eclipseSettings, _ }
  //Dependency graph plugin
  import net.virtualvoid.sbt.graph.Plugin._

  private val scala2_8 = Seq(
    "-encoding", "UTF-8"
  , "-deprecation"
  , "-optimise"
  , "-unchecked"
  , "-Xno-forwarders"
  , "-Yclosure-elim"
  , "-Ydead-code"
  , "-Yinline"
  )

  private val scala2_9 = Seq(
    "-Xmax-classfile-name", "72"
  , "-Ywarn-dead-code"
  )

  private val scala2_9_1 = Seq(
    "-Xlint"
  , "-Xverify"
  , "-Yrepl-sync"
  , "-Ywarn-inaccessible"
  , "-Ywarn-nullary-override"
  , "-Ywarn-nullary-unit"
  , "-Ywarn-numeric-widen"
  )

  private val scala2_10 = Seq(
    "-feature"
  , "-language:existentials"
  , "-language:implicitConversions"
  , "-language:postfixOps"
  , "-language:reflectiveCalls"
  , "-Ywarn-adapted-args"
  )

  private val scala2_11 = Seq(
    "-Yconst-opt"
  , "-Ywarn-infer-any"
  , "-Ywarn-unused"
  )

  lazy val scalaSettings =
    Defaults.defaultSettings ++
    eclipseSettings ++
    graphSettings ++
    Resolvers.settings ++
    Publishing.settings ++ Seq(
      organization := "io.jvm"

    , crossScalaVersions := Seq(
        /* Legacy versions, compilation requires running SBT with Java < 8 */
//        "2.8.1", "2.8.2"
//      , "2.9.0", "2.9.0-1", "2.9.1", "2.9.1-1", "2.9.2", "2.9.3"
      ) ++ Seq(
        /* Use case versions */
        "2.10.5"
      , "2.11.7"
      )
    , scalaVersion := crossScalaVersions.value.last
    , scalacOptions := scala2_8 ++ (scalaVersion.value match {
        case x if (x startsWith "2.11.")                => scala2_9 ++ scala2_9_1 ++ scala2_10 ++ scala2_11
        case x if (x startsWith "2.10.")                => scala2_9 ++ scala2_9_1 ++ scala2_10
        case x if (x startsWith "2.9.") && x >= "2.9.1" => scala2_9 ++ scala2_9_1
        case x if (x startsWith "2.9.")                 => scala2_9
        case _ => Nil
      })

    , javacOptions := Seq(
        "-encoding", "UTF-8"
      , "-deprecation"
      , "-Xlint"
      , "-target", "1.6"
      , "-source", "1.6"
      ) ++ (sys.env.get("JDK16_HOME") match {
        case Some(jdk16Home) => Seq("-bootclasspath", jdk16Home + "/jre/lib/rt.jar")
        case _ => Nil
      })

    , unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)
    , unmanagedSourceDirectories in Test    := Seq((scalaSource in Test).value)

    , EclipseKeys.executionEnvironment := Some(EclipseExecutionEnvironment.JavaSE16)
    , EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
    )

  lazy val javaSettings =
    scalaSettings ++ Seq(
      autoScalaLibrary := false
    , crossPaths := false
    , unmanagedSourceDirectories in Compile := Seq((javaSource in Compile).value)
    , EclipseKeys.projectFlavor := EclipseProjectFlavor.Java
    )
}
