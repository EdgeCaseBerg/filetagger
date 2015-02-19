
lazy val commonSettings = Seq(
	organization := "com.example",
	scalaVersion := "2.10.4",
	version := "0.0.1"
)

lazy val hello = (project in file("."))
	.settings(commonSettings:_*)
	.settings(
		name := "file-tagger",
		version := "0.0.1",
		scalacOptions ++= Seq("-feature")
	)

libraryDependencies += "org.mongodb" %% "casbah" % "2.8.0"
