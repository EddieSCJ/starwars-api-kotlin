# <p align="center"> Running </p>

You'll have two options to start the app, the first one you can run the app and environment together
in a container.

## 1. Run in console
If you want just run the app in your java environment without setup any IDE or tool, just run the following
from the app root.

```
chmod +x ./scripts/developer-mode.sh

./scripts/developer-mode.sh
```

## 2. Run in your IDE or text editing tool
Once you need debug and see some application logs, or want to use an IDE like Intellij, we strongly
recommend you to up the environment with localstack and mongodb and export the following variables to
your IDE or any tool you are using.

**Environment Variables**
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
SQS_PLANET_DELETE_URL=http://localhost:4566/queue/planet-delete.fifo
SQS_ENDPOINT=http://localhost:4566
SWAPI_URL=https://swapi.dev/api
ENV=ENV
API_PORT=8080
PROFILE=dev
MONGO_AUTH_SOURCE=admin
KAFKA_BOOTSTRAP_ADDRESS=http://localhost:9093
```

Once you set it, just run the following command:

```
chmod +x ./scripts/cache-docker/mount-environment.sh

./scripts/cache-docker/mount-environment.sh
```

After this, just start the app in your tool.
