#!/usr/bin/env bash

# Run compose to up environment
chmod +x ./scripts/cache-docker/mount-environment.sh
./scripts/cache-docker/mount-environment.sh

export KAFKA_BOOTSTRAP_ADDRESS=http://localhost:9093
export AWS_REGION=us-east-1
export MONGO_AUTH_SOURCE=admin
export MONGO_HOST=localhost
export SQS_PLANET_DELETE_URL=http://localhost:4566/queue/planet-delete.fifo
export SWAPI_URL=http://localhost:9999/starwars-api
export MONGO_DB=starwars
export SQS_ENDPOINT=http://localhost:4566
export MONGO_USER=admin
export ENV=ENV
export MONGO_CONTAINER_NAME=mongoservice
export PROFILE=test
export SNS_ENDPOINT=http://localhost:4566
export API_PORT=8080
export MONGO_PORT=27017
export MONGO_PASSWORD=password
export AWS_SECRET_KEY=SECRET
export AWS_ACCESS_KEY=ACCESS

chmod +x gradlew
./gradlew test jacocoTestReport $1 $2
