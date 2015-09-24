package com.o19s.labs.bradfordcp.solr_import_example

import com.datastax.bdp.spark.DseSparkConfHelper._
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.common.SolrInputDocument
import org.apache.spark.sql.cassandra.CassandraSQLContext
import org.apache.spark.{SparkConf, SparkContext}

object SolrImporterExample {


  def main (args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setAppName("Cassandra to Solr Import").forDse)

    // Setup and configure cassandra sql
    val cc = new CassandraSQLContext(sc)
    cc.setKeyspace("cassandra_import_example")

    // Build a joined RDD
    val nameRDD = cc.sql("SELECT n.type, n.name, r.rank FROM names n INNER JOIN ranks r ON n.name = r.name AND n.type = r.type").coalesce(50)

    // Iterate over rows inserting them
    nameRDD.foreachPartition(rows => {
      // Setup and configure Solr connection
      val solr = new CloudSolrClient("127.0.0.1:9983")
      solr.setDefaultCollection("solr_import_example")
      solr.connect()

      rows.foreach(row => {
        val document = new SolrInputDocument()
        document.addField("id", row.getString(0) + ":" + row.getString(1))
        document.addField("type", row.getString(0))
        document.addField("name", row.getString(1))
        document.addField("rank", row.getInt(2))

        solr.add(document)
      })
      solr.commit()

      solr.close()
    })
  }
}
