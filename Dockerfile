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
COPY --from=BUILD_IMAGE /root/dev/starwars/build/libs/*.jar .

EXPOSE 8080
CMD ["java","-jar","starwars*.jar"]