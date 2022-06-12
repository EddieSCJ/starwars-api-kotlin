#!/usr/bin/env bash

docker pull localstack/localstack

docker pull mongo

mkdir /tmp/cache-docker

docker save -o /tmp/cache-docker/localstack.tar localstack/localstack

docker save -o  /tmp/cache-docker/mongo.tar mongo