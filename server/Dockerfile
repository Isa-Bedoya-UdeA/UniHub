# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy root project settings and wrapper files for caching
COPY gradlew gradlew.bat ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY server server

# Create dummy app directory so settings.gradle.kts doesn't fail on include(":app")
RUN mkdir -p app

# Build shadow/fat jar for Ktor server
RUN chmod +x ./gradlew && ./gradlew :server:jar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/server/build/libs/server.jar app.jar

ENV PORT=8080
ENV HOST=0.0.0.0

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
