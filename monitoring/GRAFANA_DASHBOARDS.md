# Hướng dẫn Import Dashboard Grafana cho Dự án CNPM

## Tổng quan
Dự án sử dụng kiến trúc microservices với Spring Boot, đã có Prometheus thu thập metrics từ tất cả services qua Spring Boot Actuator.

## Các Dashboard được khuyến nghị

### 1. **Spring Boot 2.1 Statistics** (ID: 11378)
**Mục đích**: Monitor JVM, memory, threads, HTTP requests cho tất cả microservices

**Cách import**:
1. Vào Grafana: http://localhost:3000
2. Login: admin/admin
3. Vào **Dashboards** > **Import**
4. Nhập ID: `11378`
5. Chọn data source: **Prometheus**
6. Click **Import**

**Sử dụng**: 
- Tạo một dashboard cho mỗi service (user-service, restaurant-service, order-service, notification-service, api-gateway)
- Hoặc sử dụng biến `job` để switch giữa các services

---

### 2. **JVM (Micrometer)** (ID: 4701)
**Mục đích**: Monitor JVM metrics chi tiết từ Micrometer

**Cách import**: Tương tự như trên, nhập ID: `4701`

**Metrics bao gồm**:
- Memory usage (heap, non-heap)
- Garbage collection
- Threads
- Class loading

---

### 3. **Spring Boot HikariCP / JDBC Pool** (ID: 6086)
**Mục đích**: Monitor database connection pools

**Cách import**: ID: `6086`

**Hữu ích cho**: Tất cả services kết nối MySQL

---

### 4. **Resilience4j** (ID: 12006)
**Mục đích**: Monitor Circuit Breaker, Retry, Rate Limiter

**Cách import**: ID: `12006`

**Đặc biệt quan trọng cho**: API Gateway (đã cấu hình Resilience4j)

**Metrics bao gồm**:
- Circuit breaker state
- Failure rate
- Retry attempts
- Rate limiter usage

---

### 5. **MySQL Overview** (ID: 7362)
**Mục đích**: Monitor MySQL database performance

**Lưu ý**: Cần cài đặt MySQL Exporter trước

**Cách thêm MySQL Exporter vào docker-compose.yml**:
```yaml
mysql-exporter:
  image: prom/mysqld-exporter:latest
  environment:
    DATA_SOURCE_NAME: "root:2407@(mysql:3306)/"
  ports:
    - "9104:9104"
  depends_on:
    - mysql
```

**Sau đó thêm vào prometheus.yml**:
```yaml
- job_name: "mysql"
  static_configs:
    - targets: ["mysql-exporter:9104"]
```

---

### 6. **Kafka Exporter Overview** (ID: 721)
**Mục đích**: Monitor Kafka brokers, topics, consumers

**Lưu ý**: Cần cài đặt Kafka Exporter

**Cách thêm Kafka Exporter**:
```yaml
kafka-exporter:
  image: danielqsj/kafka-exporter:latest
  environment:
    KAFKA_BROKERS: kafka:9092
  ports:
    - "9308:9308"
  depends_on:
    - kafka
```

**Thêm vào prometheus.yml**:
```yaml
- job_name: "kafka"
  static_configs:
    - targets: ["kafka-exporter:9308"]
```

---

### 7. **Redis Dashboard** (ID: 11835)
**Mục đích**: Monitor Redis performance

**Lưu ý**: Cần Redis Exporter

**Cách thêm Redis Exporter**:
```yaml
redis-exporter:
  image: oliver006/redis_exporter:latest
  environment:
    REDIS_ADDR: redis:6379
  ports:
    - "9121:9121"
  depends_on:
    - redis
```

**Thêm vào prometheus.yml**:
```yaml
- job_name: "redis"
  static_configs:
    - targets: ["redis-exporter:9121"]
```

---

### 8. **Prometheus Stats** (ID: 2)
**Mục đích**: Monitor chính Prometheus server

**Cách import**: ID: `2`

---

## Dashboard Tùy chỉnh cho Microservices

### Tạo Dashboard tổng hợp cho tất cả services:

1. **Import Spring Boot Dashboard** (ID: 11378)
2. Thêm biến (Variables):
   - Name: `service`
   - Type: `Query`
   - Query: `label_values(up, job)`
   - Multi-value: Yes
   - Include All: Yes

3. Sử dụng biến `$service` trong các queries:
   ```
   http_server_requests_seconds_count{job=~"$service"}
   ```

---

## Thứ tự ưu tiên Import

### Ưu tiên cao (Import ngay):
1. ✅ **Spring Boot 2.1 Statistics** (11378) - Cho tất cả services
2. ✅ **JVM (Micrometer)** (4701) - Monitor JVM
3. ✅ **Resilience4j** (12006) - Cho API Gateway

### Ưu tiên trung bình:
4. **Spring Boot HikariCP** (6086) - Monitor DB connections
5. **Prometheus Stats** (2) - Monitor Prometheus

### Tùy chọn (cần thêm exporters):
6. **MySQL Overview** (7362) - Cần MySQL Exporter
7. **Kafka Exporter** (721) - Cần Kafka Exporter  
8. **Redis Dashboard** (11835) - Cần Redis Exporter

---

## Cấu hình Data Source

Đảm bảo Prometheus data source đã được cấu hình:
1. Vào **Configuration** > **Data Sources**
2. Click **Add data source**
3. Chọn **Prometheus**
4. URL: `http://prometheus:9090` (hoặc `http://localhost:9090` nếu chạy ngoài Docker)
5. Click **Save & Test**

---

## Tips

1. **Tạo Folder**: Tạo folder "Microservices" để nhóm các dashboard
2. **Variables**: Sử dụng variables để dễ dàng switch giữa các services
3. **Alerts**: Kết hợp với Alertmanager để nhận thông báo khi có vấn đề
4. **Annotations**: Thêm annotations khi deploy để track thay đổi performance

---

## Links Dashboard Grafana

- [Spring Boot 2.1 Statistics](https://grafana.com/grafana/dashboards/11378)
- [JVM (Micrometer)](https://grafana.com/grafana/dashboards/4701)
- [Spring Boot HikariCP](https://grafana.com/grafana/dashboards/6086)
- [Resilience4j](https://grafana.com/grafana/dashboards/12006)
- [MySQL Overview](https://grafana.com/grafana/dashboards/7362)
- [Kafka Exporter](https://grafana.com/grafana/dashboards/721)
- [Redis Dashboard](https://grafana.com/grafana/dashboards/11835)
- [Prometheus Stats](https://grafana.com/grafana/dashboards/2)

