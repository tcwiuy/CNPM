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
