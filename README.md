# Spark Summit Examples

## Retrieve Sample Data
```bash
curl http://deron.meranda.us/data/census-dist-all-last.txt.gz | gunzip - > cassandra_import_example/data/census-dist-all-last.txt
curl http://deron.meranda.us/data/census-dist-female-first.txt > cassandra_import_example/data/census-dist-female-first.txt
curl http://deron.meranda.us/data/census-dist-male-first.txt > cassandra_import_example/data/census-dist-male-first.txt
```

## Setup
1. Install DSE
   1. Download installer - http://www.datastax.com/download
   1. Run installer
1. Install Solr
   * Automated:
     1. `scripts/setup_solr.sh`
   * Manual:
     1. Download v5.2.1 - http://apache.mirrors.pair.com/lucene/solr/5.2.1/solr-5.2.1.tgz
     1. Extract package

## Running Services
1. Start DSE (w/ Spark)
   
   ```bash
   ~/dse/bin/dse cassandra -k
   ```
1. Start Solr
   
   ```bash
   cd solr
   bin/solr -c
   ```

## Loading Schema
*Ensure the services are both running*

1. DSE
   
   ```bash
   cqlsh -f cassandra_import_example/schema.cql
   ```

1. Solr
   
   ```bash
   solr/bin/solr create_collection -c solr_import_example -d solr_import_example/solr_core_config/
   ```

## Building Examples

```bash
cd cassandra_import_example && mvn package
cp ~/dse/lib/dse-4.7.0.jar solr_import_example/lib/
cd solr_import_example && sbt assembly
```

## Running Examples

```bash
java -jar cassandra_import_example/target/cassandra_import_example-1.0-SNAPSHOT-jar-with-dependencies.jar
dse spark-submit --executor-memory 1G target/scala-2.11/solr_import_example-assembly-1.0.jar
```

## Stopping Services

```bash
dse cassandra-stop
solr/bin/solr stop
```
