# First stage (build)
FROM openjdk:18.0-jdk-slim-buster AS BUILD_IMAGE

# Creating package where will be our application
ENV APP_HOME=/root/dev/starwars
WORKDIR $APP_HOME

ADD /build/libs/starwars-0.0.1-SNAPSHOT.jar $APP_HOME/starwars.jar
ENTRYPOINT ["java","-jar","starwars.jar"]