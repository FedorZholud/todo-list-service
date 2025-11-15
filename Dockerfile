# ---------- Build stage ----------
FROM gradle:8.10.0-jdk21 AS build
WORKDIR /project

# Copy Gradle wrapper & config first
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

# Then sources
COPY src ./src

# Build the Spring Boot fat jar
RUN ./gradlew clean bootJar --no-daemon

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy jar from build stage
COPY --from=build /project/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]