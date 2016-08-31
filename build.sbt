import java.io.File
import java.nio.file.{Files, Paths}

import com.trueaccord.scalapb.{ScalaPbPlugin => PB}
import DebianConstants._
import com.typesafe.sbt.packager.Keys._

name := "testTravisBuild"

version := "1.0"

scalaVersion := "2.11.8"

javaOptions in (Test,run) += "-Xmx1G -Xms1G"

scalacOptions += "-Ywarn-unused"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  "com.github.os72" % "protoc-jar" % "3.0.0-b2.1",
  "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % (PB.scalapbVersion in PB.protobufConfig).value,
  "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % (PB.scalapbVersion in PB.protobufConfig).value % PB.protobufConfig,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)


// PB Configs

PB.protobufSettings

//scalaSource in PB.protobufConfig := sourceManaged.value
//PB.flatPackage in PB.protobufConfig := true

PB.runProtoc in PB.protobufConfig := (args =>
  com.github.os72.protocjar.Protoc.runProtoc("-v300" +: args.toArray))

version in PB.protobufConfig := "3.0.0-beta-2"

// Assembly conf
assemblyJarName in assembly := "test_travis_build.jar"
assemblyMergeStrategy in assembly := {
  case path if path contains "pom." => MergeStrategy.first
  case path if path contains "javax.inject.Named" => MergeStrategy.first
  case path if path contains "mime.types" => MergeStrategy.first
  case path if path contains "org/apache/commons/logging" => MergeStrategy.first
  case path if path contains "javax/annotation/Syntax.java" => MergeStrategy.first
  case path if path contains "META-INF/jersey-module-version" => MergeStrategy.first
  case path if path contains ".properties" => MergeStrategy.filterDistinctLines
  case path if path contains ".class" => MergeStrategy.first
  case path if path contains "scalac-plugin.xml" => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

// removes all jar mappings in universal and appends the fat jar
mappings in Universal <<= (mappings in Universal, assembly in Compile) map { (mappings, fatJar) =>
  val filtered = mappings filter { case (file, name) =>  ! name.endsWith(".jar") }
  filtered :+ (fatJar -> ("lib/" + fatJar.getName))
}

// append resources to DEB
mappings in Universal <+= (packageBin in Compile, sourceDirectory ) map { (_, src) =>
  val conf = src / "main" / "resources" / "test.conf"
  conf -> "conf/test.conf"
}

// the bash scripts classpath only needs the fat jar
scriptClasspath := Seq( (assemblyJarName in assembly).value )

// Debian package buidling
enablePlugins(DebianPlugin)

maintainer := "Max Smith <max.smith@yourcompany.io>"
packageArchitecture in Debian := "amd64"
packageSummary := "Hello World Debian Package"

packageDescription := """A fun package description of our software,
  with multiple lines."""