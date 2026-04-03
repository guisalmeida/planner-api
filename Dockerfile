# --- Stage 1: Build ---
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the jar
COPY --from=build /app/target/*.jar app.jar

# PREPARE THE DATA FOLDER
# This creates /app/data inside the container
RUN mkdir ./data

# Tell Docker that this folder is a mount point
VOLUME /app/data

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]