Springboot microservice projects

# Build project 
**1. Steps to Run an Individual Service:**
1. Run eureka-service
```bash
cd eureka-service
mvn spring-boot:run
```

2. Run individual services
### user-service
```bash
cd user-service
mvn clean install
mvn spring-boot:run
```
### restaurant-service
```bash
cd restaurant-service
mvn clean install
mvn spring-boot:run
```

### order-service
```bash
cd order-service
mvn clean install
mvn spring-boot:run
```

### notification-service
```bash
cd notification-service
mvn clean install
mvn spring-boot:run
```

### api-gateway
```bash
cd api-gateway
mvn clean install
mvn spring-boot:run
```

3. Run frontend
```bash
cd frontend
npm install
npm start
```


**2. Running with Docker:**
# Build docker to use Kafka
```bash
docker-compose up -d
```
