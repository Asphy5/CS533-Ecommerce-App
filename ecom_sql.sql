CREATE DATABASE ecommerce;

USE ecommerce;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(10) DEFAULT 'user'
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    price DOUBLE
);

CREATE TABLE cart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    product_id INT,
    quantity INT
);

-- Default admin
INSERT INTO users (username, password, role)
VALUES ('admin', SHA2('admin123', 256), 'admin');

-- Sample products
INSERT INTO products (name, price) VALUES
('Laptop', 999.99),
('Phone', 499.99),
('Headphones', 79.99);