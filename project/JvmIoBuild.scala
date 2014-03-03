import sbt._
import Keys._

object BuildSettings {
  import Default._

//  ###########################################################################

  val bsUtil = javaSettings ++ Seq(
    name    := "jvm-util"
  , version := "0.0.1-SNAPSHOT"
  , initialCommands := "import io.jvm._"
  )

  val bsJsad = javaSettings ++ Seq(
    name    := "jvm-jsad"
  , version := "0.0.1-SNAPSHOT"
  , initialCommands := "import io.jvm.jsad._"
  )

  val bsXml = javaSettings ++ Seq(
    name    := "jvm-xml"
  , version := "0.0.1-SNAPSHOT"
  , initialCommands := "import io.jvm._"
  )

  val bsJson = javaSettings ++ Seq(
    name    := "jvm-json"
  , version := "0.0.1-SNAPSHOT"
  , initialCommands := "import io.jvm._"
  , libraryDependencies += "com.dslplatform" % "dsl-client-http-apache" % "0.4.14"
  )
}

//  ---------------------------------------------------------------------------

object Dependencies {
  val commonLibs = libraryDependencies <++= (version, scalaVersion) ( (v, sV) => Seq(
    "org.scala-lang" % "scala-library" % sV % (if (v endsWith "SNAPSHOT") "compile" else "test")
  , "org.scalatest" %% "scalatest" % "2.0" % "test"
  , "junit" % "junit" % "4.11" % "test"
  ))
}

//  ###########################################################################

object JvmIoBuild extends Build {
  import BuildSettings._
  import Default._
  import Dependencies._

  lazy val util = Project(
    "util"
  , file("util")
  , settings = bsUtil :+ commonLibs
  )

  lazy val jsad = Project(
    "jsad"
  , file("jsad")
  , settings = bsJsad :+ commonLibs
  )

  lazy val xml = Project(
    "xml"
  , file("xml")
  , settings = bsXml :+ commonLibs
  )

  lazy val json = Project(
    "json"
  , file("json")
  , settings = bsJson :+ commonLibs
  )
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
  , externalResolvers <<= resolvers map { r =>
      Resolver.withDefaultResolvers(r, mavenCentral = false)
    }
  )
}

//  ---------------------------------------------------------------------------

object Publishing {
  import Repositories._

  lazy val settings = Seq(
    publishTo := Some(if (version.value endsWith "SNAPSHOT") ElementSnapshots else ElementReleases)
  , credentials += Credentials(Path.userHome / ".config" / "jvm.io" / "nexus.config")
  , publishArtifact in (Compile, packageDoc) := false
  )
}

//  ---------------------------------------------------------------------------

object Default {
  //Eclipse plugin
  import com.typesafe.sbteclipse.plugin.EclipsePlugin._

  //Dependency graph plugin
  import net.virtualvoid.sbt.graph.Plugin._

  val scala2_8 = Seq(
    "-unchecked"
  , "-deprecation"
  , "-optimise"
  , "-encoding", "UTF-8"
  , "-Xcheckinit"
  , "-Xfatal-warnings"
  , "-Yclosure-elim"
  , "-Ydead-code"
  , "-Yinline"
  )

  val scala2_9 = Seq(
    "-Xmax-classfile-name", "72"
  )

  val scala2_9_1 = Seq(
    "-Yrepl-sync"
  , "-Xlint"
  , "-Xverify"
  , "-Ywarn-all"
  )

  val scala2_10 = Seq(
    "-feature"
  , "-language:postfixOps"
  , "-language:implicitConversions"
  , "-language:existentials"
  )

  lazy val scalaSettings =
    Defaults.defaultSettings ++
    eclipseSettings ++
    graphSettings ++
    Resolvers.settings ++
    Publishing.settings ++ Seq(
      organization := "io.jvm"

    , scalaVersion := crossScalaVersions.value.last
    , crossScalaVersions := Seq(
        "2.8.0", "2.8.1", "2.8.2", "2.8.3"
      , "2.9.0", "2.9.0-1", "2.9.1", "2.9.1-1", "2.9.2", "2.9.3"
      , "2.10.3"
      )
    , scalacOptions <<= scalaVersion map ( sV => scala2_8 ++ (sV match {
          case x if (x startsWith "2.10.")                => scala2_9 ++ scala2_9_1 ++ scala2_10
          case x if (x startsWith "2.9.") && x >= "2.9.1" => scala2_9 ++ scala2_9_1
          case x if (x startsWith "2.9.")                 => scala2_9
          case _ => Nil
        } )
      )

    , javaHome := sys.env.get("JDK16_HOME").map(file(_))
    , javacOptions := Seq(
        "-deprecation"
      , "-encoding", "UTF-8"
      , "-Xlint:unchecked"
      , "-source", "1.6"
      , "-target", "1.6"
      )

    , unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)
    , unmanagedSourceDirectories in Test    := Seq((scalaSource in Test).value)

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
