FROM ubuntu:latest AS build
RUN apt-get update \
    && apt-get install -y openjdk-17-jdk maven
WORKDIR /app 
COPY . .
RUN ./mvnw package -DskipTests \
    && echo "Listing files in /" \
    && ls -la / \
    && echo "Listing files in the project directory" \
    && ls -la \
    && echo "Listing files in target directory" \
    && ls -la target
FROM openjdk:17-jdk-slim
EXPOSE 8080
COPY --from=build /target/customer-product-portal-1.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]