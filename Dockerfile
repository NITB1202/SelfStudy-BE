# Sử dụng image từ OpenJDK để build ứng dụng
FROM openjdk:17-jdk-slim AS build

# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép file pom.xml và mvnw (Maven Wrapper) vào container
COPY pom.xml . 
COPY mvnw . 
COPY .mvn .mvn

# Cấp quyền thực thi cho mvnw
RUN chmod +x mvnw

# Sao chép tệp .env vào container
COPY .env .env

# Tải các dependencies của ứng dụng
RUN ./mvnw dependency:go-offline

# Sao chép mã nguồn của ứng dụng vào container
COPY src ./src

# Build ứng dụng Spring Boot (tạo file JAR)
RUN ./mvnw clean package -DskipTests

# Sử dụng một image JDK nhẹ hơn để chạy ứng dụng
FROM openjdk:17-jdk-slim

# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép file JAR từ container build vào container chạy
COPY --from=build /app/target/*.jar app.jar

# Sao chép tệp .env từ container build vào container chạy (nếu cần)
COPY --from=build /app/.env .env

# Cổng mà ứng dụng Spring Boot sẽ chạy
EXPOSE 8080

# Lệnh để chạy ứng dụng
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
