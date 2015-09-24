CREATE KEYSPACE cassandra_import_example
WITH replication = {
  'class' : 'SimpleStrategy',
  'replication_factor' : 1
};

CREATE TABLE cassandra_import_example.names (
  type VARCHAR,
  name VARCHAR,
  PRIMARY KEY ((name, type))
);

CREATE TABLE cassandra_import_example.ranks (
  type VARCHAR,
  name VARCHAR,
  rank INT,
  PRIMARY KEY((type, name))
);
