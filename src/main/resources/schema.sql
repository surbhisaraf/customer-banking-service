DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    email VARCHAR(255),
    role VARCHAR(20)
);

DROP TABLE IF EXISTS customer;

CREATE TABLE IF NOT EXISTS customer (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    phone_no VARCHAR(20),
    user_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

DROP TABLE IF EXISTS account;

CREATE TABLE IF NOT EXISTS account (
    account_no VARCHAR(255) PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    account_type VARCHAR(50) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

