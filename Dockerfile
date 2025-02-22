FROM maven:3.9-amazoncorretto-21-debian AS build
COPY . .
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

FROM openjdk:21-jdk-slim
COPY --from=build /target/maintenance-0.0.1-SNAPSHOT.jar maintenance-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "maintenance-0.0.1-SNAPSHOT.jar"]