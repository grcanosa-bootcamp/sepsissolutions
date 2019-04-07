#!/bin/bash

docker build -t cassandra_web tools/cassandra-web/

cassandra_ip=$(docker inspect --format '{{.NetworkSettings.Networks.sepsissolutions_default.IPAddress}}' sepsis_cassandra)

echo "Cassandra IP is: $cassandra_ip"

docker run --rm -e CASSANDRA_HOST=$cassandra_ip \
 -e CASSANDRA_PORT=9042 \
 -e CASSANDRA_USER="cassandra" \
 -e CASSANDRA_PASSWORD="cassandra" \
-p 3000:3000 \
--network "sepsissolutions_default" \
--name cassandra-web \
cassandra_web