# Cassandra Import Example

## Dataset
The first and last name files were pulled from http://deron.meranda.us/data/.

Each of the three files, `census-dist-all-last.txt`, `census-dist-female-first.txt`, and
`census-dist-male-first.txt` contain four items of data.  The four items
are:    

1. A "Name"
2. Frequency in percent
3. Cumulative Frequency in percent 
4. Rank

### Male First Names
```tsv
JAMES          3.318  3.318      1
JOHN           3.271  6.589      2
ROBERT         3.143  9.732      3
```

### Female First Names
```tsv
MARY           2.629  2.629      1
PATRICIA       1.073  3.702      2
LINDA          1.035  4.736      3
```

### All Last Names
```tsv
SMITH          1.006  1.006      1
JOHNSON        0.810  1.816      2
WILLIAMS       0.699  2.515      3
```

## CQL Schema
```cql
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

```