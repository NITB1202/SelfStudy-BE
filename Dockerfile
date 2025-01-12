# syntax=docker/dockerfile:1

# Stage 1: Dependency resolution
FROM eclipse-temurin:21-jdk-jammy as deps

# Set the working directory
WORKDIR /build

# Copy the Maven wrapper and project files
COPY ./mvnw /build/mvnw
COPY .mvn/ /build/.mvn/
COPY pom.xml /build/pom.xml

# Download dependencies using a cache
RUN --mount=type=cache,id=maven-cache-${BUILD_ID},target=/root/.m2 \
    ./mvnw dependency:go-offline -DskipTests

################################################################################

# Stage 2: Build the application
FROM deps as build

WORKDIR /build

# Copy the source code
COPY ./src /build/src

# Build the application
RUN --mount=type=cache,id=maven-cache-${BUILD_ID},target=/root/.m2 \
    ./mvnw package -DskipTests && \
    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar

################################################################################

# Stage 3: Extract layers for Spring Boot optimization
FROM build as extract

WORKDIR /build

# Extract the application layers
RUN java -Djarmode=layertools -jar target/app.jar extract --destination target/extracted

################################################################################

# Stage 4: Final image for running the application
FROM eclipse-temurin:21-jre-jammy as final

# Set the working directory
WORKDIR /app

# Copy environment variables (if any)
COPY .env /app/.env

# Copy extracted layers from the build stage
COPY --from=extract /build/target/extracted/dependencies/ /app/
COPY --from=extract /build/target/extracted/spring-boot-loader/ /app/
COPY --from=extract /build/target/extracted/snapshot-dependencies/ /app/
COPY --from=extract /build/target/extracted/application/ /app/

# Expose the default application port
EXPOSE 8080

# Set the default entrypoint
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]