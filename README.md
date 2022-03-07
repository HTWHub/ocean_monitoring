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

ERROR [internal] load metadata for docker.io/library/node:alpline

see [docker forum](https://forums.docker.com/t/strange-docker-output-or-help-me-please-im-very-noob/100788)

```
You have buildkit enabled and need to disable it.
If you are using Docker Desktop, open the preferences and navigate to “Docker Engine”.
Set buildkit to false and click “Apply and Restart”.
{
“features”: {
“buildkit”: false
},
“experimental”: false
}
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

| Operation                                   | Description                  |
| ------------------------------------------- | ---------------------------- |
| GET /university/student/1?\_source=name,age | Retrieves part of a document |

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

| Operation                                | Description                                                    |
| ---------------------------------------- | -------------------------------------------------------------- |
| GET /\_search                            | Search in every index and type                                 |
| GET /\_all/student/\_search              | Search in every index and only for student type                |
| GET /university/\_search                 | Search only for university index and every type                |
| GET /university/student/\_search         | Search only for university index and student type              |
| GET /university/student,teacher/\_search | Search only for university index and (student or teacher) type |
| GET /u\*,c\*/\_search                    | Search only for an index beginning with u or c and every type  |

## View the Mapping

Retrieve the mapping for type student in index university

```json
GET /university/_mapping/student?include_type_name=true
{
  "university" : {
    "mappings" : {
      "student" : {
        "properties" : {
          "age" : {
            "type" : "long"
          },
          "course" : {
            "type" : "text",
            "fields" : {
              "keyword" : {
                "type" : "keyword",
                "ignore_above" : 256
              }
            }
          },
          "degree" : {
            "type" : "text",
            "fields" : {
              "keyword" : {
                "type" : "keyword",
                "ignore_above" : 256
              }
            }
          },
        }
      }
    }
  }
}
```

## Update a Mapping

Partial update a field for type student in index university

```json
PUT /university/_mapping/student?include_type_name=true
{
  "properties": {
    "degree": {
      "type": "text"
    }
  }
}
```

## Analyzer

Analyze full-text

```json
POST _analyze
{
  "analyzer": "standard",
  "text": "The student is out of control."
}
```

and retrieve following response (short version)

```json
{
  "tokens" : [
    {
      "token" : "the",
      "start_offset" : 0,
      "end_offset" : 3,
      "type" : "<ALPHANUM>",
      "position" : 0
    },
    {
      "token" : "student",
      "start_offset" : 4,
      "end_offset" : 11,
      "type" : "<ALPHANUM>",
      "position" : 1
    }
}
```

## Query DSL

### Term filter

Filter by exact values

```json
GET /_search
{
 "query": {
   "term": {
     "age": {
       "value": 28
     }
   }
 }
}
```

### Terms filter

Specify multiple values

```json
GET /_search
{
 "query": {
   "terms": {
     "age": [18, 28, 38]
   }
 }
}
```

### Range filter

Define a range with:

- gte, gt
- lte, lt

```json
GET /_search
{
 "query": {
   "range": {
     "age": {
       "gte": 18,
       "lte": 40
     }
   }
 }
}
```

### Match Query

Search for phrase in a single field.

```json
GET /_search
{
 "query": {
   "match": {
    "name": "bob"
   }
 }
}
```

### Multi Match Query

Search for phrease in multiple fields.

```json
GET /_search
{
 "query": {
   "multi_match": {
    "fields": ["name", "email"],
    "query": "bob"
   }
 }
}
```

### Combine multiple clauses

Combine multiple match operators with clauses:

- must
- should
- must_not

```json
GET /_search
{
 "query": {
   "bool": {
     "must": [
       {
        "match": { "name": "bob" }
       }
     ],
     "should": [
       {
         "match": { "age": 28 }
       }
     ]
   }
 }
}
```

### Combining Queries with Filters

Deprecated `filtered` is replaced by `bool` operator.

```json
GET _search
{
  "query": {
    "bool": {
      "must": {
        "match": {
          "name": "bob"
        }
      },
      "filter": {
        "term": {
          "age": 28
        }
      }
    }
  }
}
```

## Aggregation DSL

Average age of students

```json
GET /university/student/_search
{
  "aggs": {
    "avg_grade": { "avg": { "field": "age" } }
  }
}
```

# Logstash

A Logstash config file has a separate section for each type of plugin you want to add to the event processing pipeline.

```
input {}
filter {}
output {}
```

## Plugins configuration

The configuration of a plugin consists of the plugin name followed by a block of settings for that plugin.

```
input {
  file {
    path => "/var/log/messages"
    type => "syslog"
  }

  file {
    path => "/var/log/apache/access.log"
    type => "apache"
  }
}
```

See [Input Plugins](https://www.elastic.co/guide/en/logstash/current/input-plugins.html), [Output Plugins](https://www.elastic.co/guide/en/logstash/current/output-plugins.html), [Filter Plugins](https://www.elastic.co/guide/en/logstash/current/filter-plugins.html), and [Codec Plugins](https://www.elastic.co/guide/en/logstash/current/codec-plugins.html).

# Docker

OS-level virtualization to deliver software in packages called containers.
Use `install-docker.sh` to install Docker Engine and Docker Compose.

## Docker File

Dockerfile specifies OS layer and execution commands.

```yaml
FROM node:14-alpine
WORKDIR '/app'
COPY package.json .
RUN npm install
COPY . .
EXPOSE 3000
CMD ["npm", "start"]
```

Build and Image from Dockerfile with `docker build -t ocean/express-example` . Verify image with `docker images` and get following output:

```
REPOSITORY                                      TAG         IMAGE ID       CREATED              SIZE
ocean/express-example                           latest      a301eea7bbe9   29 seconds ago   123MB
```

Run image with `docker run -p 3000:3000 ocean/express-example`

## Docker Compose

Run multiple Docker Container with Docker Compose

- include a Dockerfile
- include an image from registry
- port mapping
- run cycle dependencies
- Volumes and Bind Mounts
- create and share internal networks

```yaml
version: "3"
services:
	redis-server
		container_name: redis-server
		hostname: redis-server
	 	image: 'redis'
	express:
        container_name: express
        hostname: express
        build: express/
        ports:
         - "3000:3000"
        depends_on:
      		- redis-server
```

In order provide an own internal network for those containers, use a network bridge.

```yaml
version: "3"
services:
	express:
        container_name: express
        hostname: express
        build: express/
        ports:
         - "3000:3000"
       networks:
      	- es-network
networks:
  es-network:
    driver: bridge
```

Verify internal network with CLI command `docker network ls`

```
NETWORK ID     NAME                          DRIVER    SCOPE
2427c61c22ed   bridge                        bridge    local
```

Share data with **Bind Mounting** and **Volumes**

With Bind Mount, a file or directory on the _host machine_ is mounted into a container.

```yaml
heartbeat:
  container_name: heartbeat
  hostname: heartbeat
  image: "docker.elastic.co/beats/heartbeat:${ELASTIC_VERSION}"
  volumes:
    - ./heartbeat.yml:/usr/share/heartbeat/heartbeat.yml
```

With Volume, a new directory is created within Docker's storage directory on the host machine, and Docker manages that directory's content. Volumes can be easily shared over multiple containers.

```yaml
version: "3"
services:
  es01:
    container_name: es01
    hostname: es01
    image: "docker.elastic.co/elasticsearch/elasticsearch:${ELASTIC_VERSION}"
    volumes:
      - es-data01:/usr/share/elasticsearch/data
volumes:
  es-data01:
    driver: local
  es-data02:
    driver: local
```

Control those container life cycles with following commands

| Command                | Description                       |
| ---------------------- | --------------------------------- |
| docker compose up -d   | Start containers in detached mode |
| docker compose down    | Stop containers                   |
| docker ps              | List containers                   |
| docker pause [unpause] | Pause or unpause containers       |

Validate Docker Compose file with `docker-compose -f docker-compose.yml config`

## Docker Registry

Store named Docker Images on a public or private registry.

| Command                                   | Description                                                                 |
| ----------------------------------------- | --------------------------------------------------------------------------- |
| docker pull ubuntu                        | Pull an image named `ubuntu` from the official Docker Hub (short form)      |
| docker pull docker.io/library/ubuntu      | Pull an image named `ubuntu` from the official Docker Hub (longform)        |
| docker pull myregistrydomain:port/foo/bar | Pull an image named `foor/bar` from the private registry `myregistrydomain` |

## Docker Container

List all container with `docker container ls -a`.

# Prometheus

Selector and matchers:

- Equality Matcher
- Negativ Equality Matcher
- Regular expression Matcher

```
process_cpu_seconds_total{job=’node’}
process_cpu_seconds_total{job!=’node’}
Process_cpu_seconds_total{job=~”nod*”}
```

Operator

- +, -, \*, /, %, ^

```
{instance="prom-node-exporter:9100", job="node"} / 10
```

Aggregation Operator

- sum, min, max, topk, avg, stddev, count

```
sum(prometheus_http_requests_total{code="200", job="prometheus"})
sum(prometheus_http_requests_total) by (code)
```
