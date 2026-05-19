-- Quick Database Check and Setup Script
-- Run this in MySQL to ensure your database exists

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS movie_rental_system;
USE movie_rental_system;

-- Check if staff table exists and has data
SELECT 'Checking staff table...' AS Status;
SELECT COUNT(*) as staff_count FROM staff;

-- If no staff exists, insert default admin
INSERT IGNORE INTO staff (staff_id, first_name, last_name, username, password, role) 
VALUES (1, 'Admin', 'User', 'admin', 'admin123', 'admin');

-- Show available login credentials
SELECT 'Available Login Credentials:' AS Info;
SELECT username, password, role FROM staff LIMIT 5;