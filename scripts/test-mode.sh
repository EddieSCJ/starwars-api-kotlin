#!/usr/bin/env bash

# Run compose to up environment
chmod +x ./scripts/cache-docker/mount-environment.sh
./scripts/cache-docker/mount-environment.sh

export MONGO_HOST=localhost
export MONGO_USER=admin
export MONGO_PASSWORD=password
export MONGO_PORT=27017
export MONGO_DB=starwars
export MONGO_CONTAINER_NAME=mongoservice
export AWS_SECRET_KEY=SECRET
export AWS_ACCESS_KEY=ACCESS
export AWS_REGION=us-east-1
export SNS_ENDPOINT=http://localhost:4566
export SQS_ENDPOINT=http://localhost:4566
export SQS_PLANET_DELETE_URL=http://localhost:4566/queue/planet-delete.fifo
export SWAPI_URL=https://swapi.dev/api
export ENV=ENV
export API_PORT=8080
export PROFILE=test
export MONGO_AUTH_SOURCE=admin
export KAFKA_BOOTSTRAP_ADDRESS=http://localhost:9093

chmod +x gradlew
./gradlew test $1 $2
