FROM ubuntu:latest AS build

RUN apt-get update \
    && apt-get install -y openjdk-17-jdk maven

COPY . .
RUN ./mvnw package -DskipTests \
    && ls -la /target
FROM openjdk:17-jdk-slim

EXPOSE 8080
RUN ls -la /target
COPY --from=build /target/customer-product-portal-1.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]