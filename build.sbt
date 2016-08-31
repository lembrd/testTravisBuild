import com.trueaccord.scalapb.{ScalaPbPlugin => PB}

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
