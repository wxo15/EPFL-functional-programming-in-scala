course := "bigdata"
assignment := "wikipedia"

scalaVersion := "2.12.11"
scalacOptions ++= Seq("-language:implicitConversions", "-deprecation")
libraryDependencies ++= Seq(
  "com.novocode" % "junit-interface" % "0.11" % Test
)
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.3"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.3"
dependencyOverrides ++= Seq(
  ("com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7")
)

Test / testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v", "-s")
