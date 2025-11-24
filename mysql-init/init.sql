CREATE DATABASE IF NOT EXISTS user_db;
USE user_db;

CREATE TABLE users (
    user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(255),
    email VARCHAR(100) NOT NULL UNIQUE,
    is_active BIT(1) NOT NULL,
    password VARCHAR(255) NOT NULL,
    profile_image_name VARCHAR(255),
    username VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE password_reset_token (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    expiry_date DATETIME(6) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    used BIT(1) NOT NULL,
    user_id INT NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE DATABASE IF NOT EXISTS restaurant_db;
USE restaurant_db;

CREATE TABLE restaurants (
    restaurant_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    name VARCHAR(100) NOT NULL
);

CREATE TABLE menu_items (
    item_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    image_url VARCHAR(255),
    name VARCHAR(100),
    price DECIMAL(10,2),
    stock INT,
    restaurant_id INT NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id) ON DELETE CASCADE
);

CREATE DATABASE IF NOT EXISTS order_db;
USE order_db;

CREATE TABLE orders (
    order_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    contact_email VARCHAR(50),
    contact_phone VARCHAR(20),
    is_deleted BIT(1) NOT NULL,
    order_date DATETIME(6) NOT NULL,
    payment_status ENUM('FAILED','PAID','PENDING','REFUNDED') NOT NULL,
    recipient_name VARCHAR(50),
    shipping_address VARCHAR(255),
    status ENUM('CANCELLED','DELIVERED','PENDING','PREPARING','READY') NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    user_id INT,
    payment_method VARCHAR(20)
);

CREATE TABLE order_items (
    order_item_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    menu_item_id INT NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    order_id INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

CREATE DATABASE IF NOT EXISTS notification_db;
USE notification_db;
CREATE TABLE notifications (
    notification_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read BIT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

-- Create user if not exists (Docker may have already created it via MYSQL_USER)
CREATE USER IF NOT EXISTS 'kyvy'@'%' IDENTIFIED BY '2407';

-- Grant all privileges on all databases 
-- (Docker automatically grants privileges on MYSQL_DATABASE only, 
--  but we need privileges on all databases for microservices)
GRANT ALL PRIVILEGES ON *.* TO 'kyvy'@'%' WITH GRANT OPTION;

-- Apply privilege changes immediately
FLUSH PRIVILEGES;
