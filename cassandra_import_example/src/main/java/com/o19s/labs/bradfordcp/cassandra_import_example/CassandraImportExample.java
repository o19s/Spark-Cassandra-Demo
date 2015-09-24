package com.o19s.labs.bradfordcp.cassandra_import_example;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.MetricRegistry;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class CassandraImportExample
{

  public void performImport(){
    // Setup metrics collection
    MetricRegistry metrics = new MetricRegistry();
    ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();
    reporter.start(1, TimeUnit.SECONDS);
    Timer insertsTimer = metrics.timer("Inserts Timer");
    Timer jobTimer = metrics.timer("Job Timer");

    Timer.Context jobContext = jobTimer.time();
    try {
      // Connect to the cluster
      Cluster cluster = Cluster.builder()
          .addContactPoint("127.0.0.1")
          .build();
      Session session = cluster.connect();

      import_file(session, insertsTimer, "first", "data/census-dist-female-first.txt");
      import_file(session, insertsTimer, "first", "data/census-dist-male-first.txt");
      import_file(session, insertsTimer, "last", "data/census-dist-all-last.txt");

      // Disconnect from the cluster
      session.close();
      cluster.close();
    } finally {
      jobContext.stop();
    }

    // Stop collecting metrics
    reporter.report();
    reporter.stop();
  }

  private void import_file(Session session, Timer timer, String nameType, String filename) {
    PreparedStatement insertNamesStatement = session
        .prepare("INSERT INTO cassandra_import_example.names (type, name) VALUES (?, ?)")
        .setConsistencyLevel(ConsistencyLevel.ANY);
    PreparedStatement insertRanksStatement = session
        .prepare("INSERT INTO cassandra_import_example.ranks (type, name, rank) VALUES (?, ?, ?)")
        .setConsistencyLevel(ConsistencyLevel.ANY);
    Pattern namePattern = Pattern.compile("(\\w+)\\s+(\\d+\\.\\d+)\\s+(\\d+\\.\\d+)\\s+(\\d+)");

    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      for (String line; (line = br.readLine()) != null; ) {
        Matcher m = namePattern.matcher(line);

        if (m.matches()){
          final Timer.Context namesContext = timer.time();
          try {
            session.executeAsync(insertNamesStatement.bind(nameType, m.group(1)));
          } finally {
            namesContext.stop();
          }

          final Timer.Context ranksContext = timer.time();
          try {
            session.executeAsync(insertRanksStatement.bind(nameType, m.group(1), new Integer(m.group(4))));
          } finally {
            ranksContext.stop();
          }
        }
        else {
          System.err.println("No match for: " + line);
          break;
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void main( String[] args )
  {
    CassandraImportExample importer = new CassandraImportExample();
    importer.performImport();
  }
}
