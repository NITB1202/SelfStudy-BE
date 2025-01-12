# syntax=docker/dockerfile:1.4

################################################################################

# Create a stage for resolving and downloading dependencies.
FROM eclipse-temurin:21-jdk-jammy as deps

WORKDIR /build

# Copy the mvnw wrapper with executable permissions.
COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/

# Download dependencies using cache for Maven repository.
RUN --mount=type=cache,id=maven-cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -DskipTests

################################################################################

# Create a stage for building the application based on the stage with downloaded dependencies.
FROM deps as package

WORKDIR /build

COPY pom.xml pom.xml
COPY ./src src/

# Build the application using Maven cache.
RUN --mount=type=cache,id=maven-cache,target=/root/.m2 \
    ./mvnw package -DskipTests && \
    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar

################################################################################

# Create a stage for extracting the application into separate layers.
FROM package as extract

WORKDIR /build

RUN java -Djarmode=layertools -jar target/app.jar extract --destination target/extracted

################################################################################

# Create a new stage for running the application.
FROM eclipse-temurin:21-jre-jammy AS final

COPY .env /app/.env

WORKDIR /app

# Copy the executable from the "package" stage.
COPY --from=extract /build/target/extracted/dependencies/ ./
COPY --from=extract /build/target/extracted/spring-boot-loader/ ./
COPY --from=extract /build/target/extracted/snapshot-dependencies/ ./
COPY --from=extract /build/target/extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
