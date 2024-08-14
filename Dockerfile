
FROM eclipse-temurin:22-jdk AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM eclipse-temurin:22-jdk

ARG JAR_FILE=/build/libs/FileDrive-0.0.1-SNAPSHOT.jar


COPY ${JAR_FILE} app.jar
EXPOSE 8080

LABEL authors="Vadim"
ENTRYPOINT ["java", "-jar", "/app.jar"]