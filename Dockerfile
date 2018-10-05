# Development
FROM openjdk:8-jdk-alpine
COPY db-configs.properties db-configs.properties
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/dash-browser-automation-0.1.0.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
