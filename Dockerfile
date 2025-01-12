# syntax=docker/dockerfile:1

# Định nghĩa argument cho BUILD_ID
ARG BUILD_ID

# Stage 1: Dependency resolution
FROM eclipse-temurin:21-jdk-jammy as deps

WORKDIR /build

COPY ./mvnw /build/mvnw
COPY .mvn/ /build/.mvn/
COPY pom.xml /build/pom.xml

# Sử dụng BUILD_ID làm cache key
RUN --mount=type=cache,id=maven-cache-${BUILD_ID},target=/root/.m2 \
    ./mvnw dependency:go-offline -DskipTests

################################################################################

# Stage 2: Build the application
FROM deps as build

WORKDIR /build

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

WORKDIR /app

COPY .env /app/.env

COPY --from=extract /build/target/extracted/dependencies/ /app/
COPY --from=extract /build/target/extracted/spring-boot-loader/ /app/
COPY --from=extract /build/target/extracted/snapshot-dependencies/ /app/
COPY --from=extract /build/target/extracted/application/ /app/

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
