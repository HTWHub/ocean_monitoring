# ELK with Docker

## Features
 - Multi-node elastic search cluster
 - Kibana 
 - Jira

## Getting started

Run `docker-compose` to bring up the cluster:
```
docker-compose up
```

Submit a `_cat/nodes` request to see that the nodes are up and running:
```
curl -X GET "localhost:9200/_cat/nodes?v=true&pretty"
```

Kibana should be available 
[http://localhost:5601](http://localhost:5601)

# Resolved issues

Max virtual memory areas vm.max_map_count [65530] is too low..

See [docker-prod-prerequisites](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html#docker-prod-prerequisites)
```
wsl -d docker-desktop // windows
sysctl -w vm.max_map_count=262144
```

# Elasticsearch

## Indexing Documents

Add a student to university with `PUT` or `POST`:

```json
PUT /university/student/1
{
    "name": "Bob Son",
    "course": "CS",
    "degree": "MSc",
    "email": "bobson@university.edu",
    "age": 28,
    "enrolled_date": "2015-01-19",
    "remarks": "This student is out of control.",
    "interests": ["coding", "gaming", "party"] 
}
```

Other options for indexing

| Operation                                | Description                                                  |
| ---------------------------------------- | ------------------------------------------------------------ |
| POST /university/student/                | Creates a document with an auto-generated id and overwrites. |
| PUT /university/student/1_create         | Returns an already exists exception                          |
| PUT /university/student/1?op_type=create | Returns an already exists exception                          |

## Retrieving Documents

Retrieve a student from university:

```json
GET /university/student/1
...
{
  "_index" : "university",
  "_type" : "student",
  "_id" : "1",
  "_version" : 1,
  "_seq_no" : 0,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "name" : "Bob Son",
    "course" : "CS",
    "degree" : "MSc",
    "email" : "bobson@university.edu",
    "age" : 28,
    "enrolled_date" : "2015-01-19",
    "remarks" : "This student is out of control.",
    "interests" : [
      "coding",
      "gaming",
      "party"
    ]
  }
}
```

Other options for retrieving documents

| Operation                                  | Description                  |
| ------------------------------------------ | ---------------------------- |
| GET /university/student/1?_source=name,age | Retrieves part of a document |

## Basic Search

Search only students in every university:

```json
GET /_all/student/_search
{
  "_shards" : {
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "university",
        "_type" : "student",
        "_id" : "1",
        "_score" : 1.0,
        "_source" : {
          "name" : "Bob Son",
          "course" : "CS",
          "degree" : "MSc",
          "email" : "bobson@university.edu",
          "age" : 28,
          "enrolled_date" : "2015-01-19",
          "remarks" : "This student is out of control.",
          "interests" : [
            "coding",
            "gaming",
            "party"
          ]
        }
      }
    ]
  }
}
```

Other Options for basic search

| Operation                               | Description                                                  |
| --------------------------------------- | ------------------------------------------------------------ |
| GET /_search                            | Search in every index and type                               |
| GET /\_all/student/_search              | Search in every index and only for student type              |
| GET /university/_search                 | Search only for university index and every type              |
| GET /university/student/_search         | Search only for university index and student type            |
| GET /university/student,teacher/_search | Search only for university index and (student or teacher) type |
| GET /u\*,c\*/_search                    | Search only for an index beginning with u or c and every type |

