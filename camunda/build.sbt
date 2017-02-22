name := "camunda"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "0.10.1.1",
  "org.apache.kafka" % "kafka-streams" % "0.10.1.1",
  "org.camunda.bpm" % "camunda-bom" % "7.6.0",
  "org.camunda.bpm" % "camunda-engine" % "7.6.0",
  "com.h2database" % "h2" % "1.4.192",
  "com.typesafe.play" %% "play-json" % "2.5.12"
)
