# <p align="center"> <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kotlin/kotlin-plain-wordmark.svg" width=100 /> <br> :space_invader: Star Wars API :space_invader:</p>


#### <p align="center"> Project Tooling </p>

<div align="center"> 
    <p> Kotlin</p>
    <a href="https://app.snyk.io/org/eddiescj/projects" target="_blank">:wolf: Snyk </a>
    <a href="https://sonarcloud.io/summary/new_code?id=EddieSCJ_starwars-api-kotlin" target="_blank">:detective: SonarCloud </a>
    <a href="https://app.codecov.io/gh/EddieSCJ/starwars-api-kotlin/" target="_blank">:open_umbrella: CodeCov </a>
</div>

#### <p align="center"> Requirements </p>

<div align="center"> 
    <a href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html" target="_blank">:coffee: Java 17 </a>
    <a href="https://docs.docker.com/get-docker/" target="_blank"> :whale: Docker </a>
    <a href="https://docs.docker.com/get-docker/" target="_blank"> <img width="13" src="https://raw.githubusercontent.com/davzoku/emoji.ico/master/devicon/mongodb-original.ico"> MongoDB </a>
</div>

#### <p align="center"> Documentation </p> 
For documentation purposes, just open the following link: [Star Wars API Kotlin Documentation](https://eddiescj.github.io/starwars-api-kotlin/){:target="_blank"}


#### <p align="center"> Description </p>

This api is a simple wrapper for the [Star Wars API](https://swapi.dev/) where you can get information about the characters,
planets, starships, vehicles, species, films, and more with a few extra features where you can handle this data however you
need.

The api also is documented with [OpenAPI Swagger](https://swagger.io/specification/), so if you have any doubt, just open in your browser the follow url: `http://domain-you-are-using/api/v0/swagger-ui/index.html`

Please, read the content below to know how to use this api and if is there any doubt, please, contact me.

#### <p align="center"> Running </p>

You'll have two options to start the app, the first one you can run the app and environment together
in a container.

#### 1. Run in console
If you want just run the app in your java environment without setup any IDE or tool, just run the following
from the app root.

```
chmod +x ./scripts/developer-mode.sh

./scripts/developer-mode.sh
```

#### 2. Run in your IDE or text editing tool
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

#### <p align="center"> Testing </p>

#### 1. Run Tests in console

Execute the following commands
```
chmod +x ./scripts/test-mode.sh

./scripts/test-mode.sh
```

#### 2. Run Tests in your IDE or text editing tool

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

Once you set it, just run the following command:

```
chmod +x ./scripts/cache-docker/mount-environment.sh

./scripts/cache-docker/mount-environment.sh
```

After done it to your tool, just run the tests in your tool.
