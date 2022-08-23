# First stage (build)
FROM openjdk:18.0-jdk-slim-buster AS BUILD_IMAGE
WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY src src

RUN ./gradlew build -x test
RUN mkdir -p build/libs/dependency && (cd build/libs/dependency; jar -xf ../*.jar)

FROM openjdk:18.0-jdk-slim-buster AS RUNTIME_IMAGE
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/libs/dependency
COPY --from=BUILD_IMAGE ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=BUILD_IMAGE ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=BUILD_IMAGE ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.starwars.kotlin.StarWarsApiKotlinApplicationKt.kt"]