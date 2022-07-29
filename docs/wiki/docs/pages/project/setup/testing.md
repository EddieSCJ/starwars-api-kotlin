# <p align="center"> Testing </p>

## 1. Run Tests in console

Execute the following commands
```
chmod +x ./scripts/test-mode.sh

./scripts/test-mode.sh
```

## 2. Run Tests in your IDE or text editing tool

Export the following env vars to your tool:
```bash
MONGO_HOST=localhost
MONGO_USER=admin
MONGO_PASSWORD=password
MONGO_PORT=27017
MONGO_DB=starwars
MONGO_CONTAINER_NAME=mongoservice
AWS_SECRET_KEY=SECRET
AWS_ACCESS_KEY=ACCESS
AWS_REGION=us-east-1
SNS_ENDPOINT=http://localhost:4566
SQS_ENDPOINT=http://localhost:4566
SQS_PLANET_DELETE_URL=http://localhost:4566/queue/planet-delete.fifo
SWAPI_URL=https://swapi.dev/api
ENV=ENV
API_PORT=8080
PROFILE=test
MONGO_AUTH_SOURCE=admin
KAFKA_BOOTSTRAP_ADDRESS=http://localhost:9093
```