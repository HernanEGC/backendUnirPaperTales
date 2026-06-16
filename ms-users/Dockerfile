FROM maven:3.9.12-eclipse-temurin-25 AS build
COPY . .
RUN mvn clean package

FROM eclipse-temurin:25-jre
EXPOSE 8083
COPY --from=build /target/users-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

