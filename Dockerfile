# First stage (build)
FROM openjdk:18.0-jdk-slim-buster AS BUILD_IMAGE

# Creating package where will be our application
ENV APP_HOME=/root/dev/starwars/
WORKDIR $APP_HOME

# Copying gradle configs to package
COPY build.gradle.kts gradlew gradlew.bat $APP_HOME
COPY gradle $APP_HOME/gradle

# download dependencies
RUN chmod +x gradlew
RUN ./gradlew build -x :bootJar -x test

# copying dependecies
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build -x test

# Second stage (run)
# Using jdk (necessary to run the build jar)
FROM openjdk:18.0-jdk-slim-buster AS RUN_IMAGE
WORKDIR /root/

#Copying our jar from the first stage
COPY --from=BUILD_IMAGE /root/dev/myapp/build/libs/*.jar .

EXPOSE 8080
CMD ["java","-jar","starwars*.jar"]
#
## First stage (build)
#FROM openjdk:18.0-jdk-slim-buster AS BUILD_IMAGE
#WORKDIR /workspace/app
#
#COPY gradle gradle
#COPY build.gradle.kts settings.gradle.kts gradlew ./
#COPY src src
#
#RUN ./gradlew build -x test
#RUN mkdir -p build/libs/dependency && (cd build/libs/dependency; jar -xf ../*.jar)
#
#FROM openjdk:18.0-jdk-slim-buster AS RUNTIME_IMAGE
#VOLUME /tmp
#ARG DEPENDENCY=/workspace/app/build/libs/dependency
#COPY --from=BUILD_IMAGE ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY --from=BUILD_IMAGE ${DEPENDENCY}/META-INF /app/META-INF
#COPY --from=BUILD_IMAGE ${DEPENDENCY}/BOOT-INF/classes /app
#ENTRYPOINT ["java","-cp","app:app/lib/*","com.starwars.kotlin.StarWarsApiKotlinApplicationKt.kt"]