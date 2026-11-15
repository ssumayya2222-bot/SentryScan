-- SentryScan website database
-- Import in phpMyAdmin: open the SQL tab, paste this file and click Go.

CREATE DATABASE IF NOT EXISTS sentryscan_web
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sentryscan_web;

-- Registered users. Passwords are stored as hashes, never as plain text.
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Messages sent from the Contact Us page.
CREATE TABLE IF NOT EXISTS contact_messages (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  message TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
