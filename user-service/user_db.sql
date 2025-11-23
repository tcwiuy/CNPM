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
