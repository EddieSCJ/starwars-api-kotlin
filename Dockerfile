FROM openjdk:18.0-jdk-slim-buster

MAINTAINER Edcleidson Junior
COPY build/libs/*.jar starwars.jar

EXPOSE 8080
CMD ["java","-jar","starwars.jar"]