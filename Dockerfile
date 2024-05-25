#FROM amazoncorretto:21-alpine3.16-jdk AS builder
#
## Set the working directory in the container
#WORKDIR /app
#
## Copy the Gradle wrapper files
#COPY gradlew .
#COPY gradle ./gradle
#
## Copy only the necessary files for dependency resolution
#COPY build.gradle.kts settings.gradle.kts ./
#
## Download and cache dependencies
#RUN ./gradlew dependencies
#
## Copy the entire project (except build directories) for building
#COPY . .
#
## Build the application using Gradle wrapper
#RUN ./gradlew clean build --no-daemon

# Second stage: create the final container image
FROM amazoncorretto:21-alpine3.16 AS final

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY  /build/libs/*.jar ./app.jar

# Expose the port
EXPOSE 8003

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
