-- Movie Rental System - User Setup Script
-- This script creates test accounts for admin and staff roles

-- Make sure the staff table exists with proper structure
CREATE TABLE IF NOT EXISTS staff (
    staff_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Clear existing test accounts (optional - comment out if you want to keep existing)
DELETE FROM staff WHERE username IN ('admin', 'john', 'jane', 'bob');

-- Insert test accounts
INSERT INTO staff (first_name, last_name, username, password, role) VALUES
-- ADMIN ACCOUNTS
('Admin', 'User', 'admin', 'admin123', 'admin'),
('Gerald', 'Administrator', 'gerald', 'admin456', 'admin'),

-- STAFF ACCOUNTS  
('John', 'Doe', 'john', 'staff123', 'staff'),
('Jane', 'Smith', 'jane', 'staff456', 'staff'),
('Bob', 'Wilson', 'bob', 'staff789', 'staff');

-- Verify the accounts were created
SELECT staff_id, first_name, last_name, username, role, date_created 
FROM staff 
ORDER BY role, staff_id;

-- Show role distribution
SELECT role, COUNT(*) as count 
FROM staff 
GROUP BY role;