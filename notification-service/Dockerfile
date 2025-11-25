# --- Giai đoạn 1: Build code (Dùng image Maven) ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
# Lệnh này sẽ tự tải thư viện và đóng gói thành file .jar ngay trong Docker
# Thêm retry mechanism để xử lý lỗi tạm thời từ Maven Central (500 error)
# -U: force update dependencies
# --fail-at-end: tiếp tục build các module khác nếu một module fail
RUN mvn clean package -DskipTests -U --fail-at-end || \
    (echo "First attempt failed, retrying in 10 seconds..." && sleep 10 && mvn clean package -DskipTests -U --fail-at-end) || \
    (echo "Second attempt failed, retrying in 15 seconds..." && sleep 15 && mvn clean package -DskipTests -U --fail-at-end)

# --- Giai đoạn 2: Chạy ứng dụng (Dùng image Java nhẹ) ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copy file .jar từ giai đoạn 1 (build) sang giai đoạn 2
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]