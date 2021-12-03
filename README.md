# ELK with Docker

## Features
 - Multi-node elastic search cluster

## Getting started

Run `docker-compose` to bring up the cluster:
```
docker-compose up
```

Submit a `_cat/nodes` request to see that the nodes are up and running:
```
curl -X GET "localhost:9200/_cat/nodes?v=true&pretty"
```


# Resolved issues

Max virtual memory areas vm.max_map_count [65530] is too low..

See [docker-prod-prerequisites](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html#docker-prod-prerequisites)
```
wsl -d docker-desktop // windows
sysctl -w vm.max_map_count=262144
```