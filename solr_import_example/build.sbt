name := "solr_import_example"

version := "1.0"

scalaVersion := "2.11.7"

// Scala Dependencies
libraryDependencies += "com.datastax.spark" % "spark-cassandra-connector-java_2.10" % "1.2.1" % "provided" withSources() withJavadoc()
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.2.1" % "provided" withSources() withJavadoc()
libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.2.1" % "provided" withSources() withJavadoc()

// Java Dependencies
// libraryDependencies += "com.datastax" % "dse" % "4.7.0" // Placed in the lib directory
libraryDependencies += "org.apache.solr" % "solr-solrj" % "5.2.1" withSources() withJavadoc()

// META-INF discarding
mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
  }
}
