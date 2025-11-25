# Food Ordering System - Microservices Architecture

Hệ thống đặt món ăn được xây dựng bằng kiến trúc microservices với Spring Boot, bao gồm các dịch vụ: User Service, Restaurant Service, Order Service, Notification Service, API Gateway và Frontend Angular.

## 🛠 Technology Stack

- **Backend**: Spring Boot, Spring Cloud (Eureka, Gateway), Spring Security
- **Frontend**: Angular 17, Tailwind CSS
- **Database**: MySQL 8.0
- **Message Queue**: Apache Kafka
- **Cache**: Redis
- **Service Discovery**: Netflix Eureka
- **Monitoring**: Prometheus, Grafana, Alertmanager
- **Containerization**: Docker, Docker Compose

## 📋 Prerequisites

Trước khi chạy dự án, đảm bảo bạn đã cài đặt:

- **Java**: JDK 17 hoặc cao hơn
- **Maven**: 3.6+ 
- **Node.js**: 18+ (cho Angular 17)
- **npm**: Đi kèm với Node.js
- **Docker**: 20.10+
- **Docker Compose**: 2.0+

Kiểm tra phiên bản:
```bash
java -version
mvn -version
node -v
npm -v
docker --version
docker-compose --version
```

## 🏗 Architecture Overview

```
┌─────────────┐
│   Frontend  │ (Angular - Port 80/4200)
└──────┬──────┘
       │
┌──────▼──────────┐
│  API Gateway    │ (Port 9000)
└──────┬──────────┘
       │
   ┌───┴───┬──────────┬──────────────┐
   │       │          │              │
┌──▼──┐ ┌─▼──┐  ┌────▼────┐  ┌──────▼──────┐
│User │ │Rest│  │  Order  │  │Notification │
│Serv │ │Serv│  │  Serv   │  │    Serv     │
└──┬──┘ └─┬──┘  └────┬────┘  └──────┬──────┘
   │      │          │              │
   └──────┴──────────┴──────────────┘
          │
    ┌─────┴─────┐
    │  Eureka   │ (Port 8761)
    │  Service  │
    └───────────┘
```

## 🔌 Service Ports

| Service | Port | Description |
|---------|------|-------------|
| Eureka Service | 8761 | Service Discovery |
| API Gateway | 9000 | API Gateway |
| User Service | 8081 | User Management |
| Restaurant Service | 8082 | Restaurant & Menu Management |
| Order Service | 8083 | Order Management |
| Notification Service | 8084 | Notification Service |
| Frontend | 80 (Docker) / 4200 (Dev) | Angular Frontend |
| MySQL | 3306 | Database |
| Kafka | 29092 | Message Broker |
| Redis | 6379 | Cache |
| Prometheus | 9090 | Metrics Collection |
| Grafana | 3000 | Monitoring Dashboard |
| Alertmanager | 9093 | Alert Management |

## 🌐 Service URLs

Khi chạy với Docker:
- **Frontend**: http://localhost
- **API Gateway**: http://localhost:9000
- **Eureka Dashboard**: http://localhost:8761
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090
- **Alertmanager**: http://localhost:9093

Khi chạy local development:
- **Frontend**: http://localhost:4200
- **API Gateway**: http://localhost:9000
- **Eureka Dashboard**: http://localhost:8761

## 📦 Build Project

### **1. Steps to Run an Individual Service:**

**Lưu ý**: Thứ tự khởi động các service rất quan trọng. Luôn khởi động Eureka Service trước.

#### 1.1. Run Eureka Service (Bắt buộc chạy đầu tiên)
```bash
cd eureka-service
mvn spring-boot:run
```

Đợi Eureka Service khởi động hoàn toàn (thường mất 30-60 giây) trước khi chạy các service khác.

#### 1.2. Run Individual Services

##### User Service
```bash
cd user-service
mvn clean install
mvn spring-boot:run
```

##### Restaurant Service
```bash
cd restaurant-service
mvn clean install
mvn spring-boot:run
```

##### Order Service
```bash
cd order-service
mvn clean install
mvn spring-boot:run
```

##### Notification Service
```bash
cd notification-service
mvn clean install
mvn spring-boot:run
```

##### API Gateway (Chạy sau khi các microservices đã khởi động)
```bash
cd api-gateway
mvn clean install
mvn spring-boot:run
```

#### 1.3. Run Frontend
```bash
cd frontend
npm install
npm start
```

Frontend sẽ chạy tại http://localhost:4200

### **2. Running with Docker:**

#### 2.1. Prerequisites
Đảm bảo Docker và Docker Compose đã được cài đặt và đang chạy.

#### 2.2. Start All Services
```bash
docker-compose up -d
```

Lệnh này sẽ khởi động tất cả các services bao gồm:
- Infrastructure: Zookeeper, Kafka, Redis, MySQL
- Microservices: Eureka, API Gateway, User Service, Restaurant Service, Order Service, Notification Service
- Frontend
- Monitoring: Prometheus, Grafana, Alertmanager

## 🗄 Database Setup

### Automatic Setup (Docker)
Khi chạy với Docker, database sẽ được tự động khởi tạo từ file `mysql-init/init.sql`. Các database sau sẽ được tạo:

- `user_db`: Quản lý users và authentication
- `restaurant_db`: Quản lý restaurants và menu items
- `order_db`: Quản lý orders và order items
- `notification_db`: Quản lý notifications

### Manual Setup (Local Development)
Nếu chạy local không dùng Docker:

1. Tạo MySQL database và user:
```sql
CREATE DATABASE user_db;
CREATE DATABASE restaurant_db;
CREATE DATABASE order_db;
CREATE DATABASE notification_db;
```

2. Chạy các file SQL initialization:
   - `user-service/user_db.sql`
   - `restaurant-service/restaurant_db.sql`
   - `order-service/order_db.sql`
   - Database schema cho notification được tạo tự động

3. Cấu hình database connection trong `application.yml` của mỗi service.

### Database Credentials (Docker)
- **Username**: kyvy
- **Password**: 2407
- **Root Password**: 2407
- **Host**: localhost (khi chạy Docker) hoặc localhost:3306

## ⚙️ Configuration

### Environment Variables
Các service sử dụng cấu hình từ file `application.yml` hoặc `application.properties`. Khi chạy với Docker, một số biến môi trường được set trong `docker-compose.yml`.

### Kafka Configuration
Kafka được sử dụng cho async communication giữa các services, đặc biệt cho notification service.

- **Bootstrap Server**: `localhost:29092` (Docker) hoặc `localhost:9092` (local)
- **Zookeeper**: `localhost:22181` (Docker)

### Redis Configuration
Redis được sử dụng cho caching và rate limiting trong API Gateway.

- **Host**: `localhost`
- **Port**: `6379`

## 📊 Monitoring

Dự án bao gồm monitoring stack với Prometheus, Grafana và Alertmanager:

### Prometheus
- **URL**: http://localhost:9090
- Thu thập metrics từ tất cả microservices
- Cấu hình: `monitoring/prometheus/prometheus.yml`

### Grafana
- **URL**: http://localhost:3000
- **Username**: admin
- **Password**: admin
- Import dashboards để visualize metrics

### Alertmanager
- **URL**: http://localhost:9093
- Quản lý alerts từ Prometheus
- Cấu hình: `monitoring/alertmanager/alertmanager.yml`

## 📁 Project Structure

```
CNPM/
├── api-gateway/          # API Gateway service
├── eureka-service/       # Service Discovery
├── user-service/         # User management service
├── restaurant-service/   # Restaurant & menu service
├── order-service/        # Order management service
├── notification-service/ # Notification service
├── frontend/             # Angular frontend
├── monitoring/           # Prometheus & Alertmanager configs
├── mysql-init/           # Database initialization scripts
├── docker-compose.yml    # Docker Compose configuration
└── README.md             # This file
```

## 📝 Additional Notes

- Khi chạy local development, đảm bảo tất cả infrastructure services (MySQL, Kafka, Redis) đã được khởi động
- Để build Docker images riêng, xem Dockerfile trong mỗi service directory
- Xem file `SECRETS_SETUP.md` để biết cách setup CI/CD secrets.
