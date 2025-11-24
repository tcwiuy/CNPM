# --- Chỉ chạy ứng dụng (JAR đã được build trong CI) ---
ARG BASE_IMAGE=eclipse-temurin:21-jdk-alpine
FROM ${BASE_IMAGE}
WORKDIR /app
# Copy file .jar đã được build sẵn từ CI
COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]