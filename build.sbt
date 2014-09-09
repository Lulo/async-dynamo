organization := "asyncdynamo"

name := "async-dynamo"

version := "1.7.3"

scalaVersion := "2.10.2"

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-Xcheckinit")

publishTo := Some(Resolver.file("local repo",Path.userHome / ".m2/repository" asFile))

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "OSS Sonatype" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "Sonatype Nexus releases" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "Sonatype Nexus snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "maven central" at "http://repo1.maven.org/maven2/"

resolvers += "piotrga-remote" at "https://raw.github.com/piotrga/piotrga.github.com/master/maven-repo"

libraryDependencies ++= Seq(
    "com.amazonaws" % "aws-java-sdk" % "1.4.7",
    "com.typesafe.akka" %% "akka-actor" % "2.2.1",
    "org.scalatest" %% "scalatest" % "1.9.1" % "test",
    "log4j" % "log4j" % "1.2.17" % "test",
    "monitoring" %% "monitoring" % "1.4.0" % "test"
)

