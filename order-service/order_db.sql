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
