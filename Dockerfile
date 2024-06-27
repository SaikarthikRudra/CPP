# Use an official Maven image to build the application
FROM maven:3.8.5-openjdk-17-slim AS build
 
# Copy the source code into the container
COPY . /app
WORKDIR /app
 
# Build the application
RUN mvn clean package -DskipTests
 
# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine
 
# Set the working directory in the container
WORKDIR /app
 
# Copy the built jar file from the build stage
COPY --from=build /app/target/customer-product-portal-0.0.1-SNAPSHOT.jar app.jar
#/exavalu-customer-product-portal/target/customer-product-portal-0.0.1-SNAPSHOT.jar
# Expose the port that the application will run on (optional, for documentation purposes)
#EXPOSE 8080
 
# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]