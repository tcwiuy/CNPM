# --- Giai đoạn 1: Build code (Dùng image Maven) ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
# Lệnh này sẽ tự tải thư viện và đóng gói thành file .jar ngay trong Docker
RUN mvn clean package -DskipTests

# --- Giai đoạn 2: Chạy ứng dụng (Dùng image Java nhẹ) ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copy file .jar từ giai đoạn 1 (build) sang giai đoạn 2
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]