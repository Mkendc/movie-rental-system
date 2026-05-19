-- Movie Rental System Database Setup Script
-- Run this script in your MySQL database to set up all tables

-- Create database
CREATE DATABASE IF NOT EXISTS movie_rental_system;
USE movie_rental_system;

-- Drop tables if they exist (to start fresh)
DROP TABLE IF EXISTS rental_items;
DROP TABLE IF EXISTS rentals;
DROP TABLE IF EXISTS movies;
-- Note: Customer and staff tables should already exist from your current setup

-- Create movies table
CREATE TABLE movies (
    movie_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    release_year INT,
    genre VARCHAR(100),
    rating DECIMAL(3,1),
    rental_fee DECIMAL(10,2),
    available_copies INT DEFAULT 1,
    total_copies INT DEFAULT 1,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create rentals table
CREATE TABLE rentals (
    rental_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    staff_id INT DEFAULT NULL,
    rental_date DATE NOT NULL,
    return_date DATE NOT NULL,
    returned_date DATE NULL,
    total_cost DECIMAL(10,2),
    deposit DECIMAL(10,2) DEFAULT 0,
    balance DECIMAL(10,2),
    late_fees DECIMAL(10,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'Active',
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE SET NULL
);

-- Create rental_items table (junction table for many-to-many relationship)
CREATE TABLE rental_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    rental_id INT NOT NULL,
    movie_id INT NOT NULL,
    rental_fee DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (rental_id) REFERENCES rentals(rental_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE
);

-- Insert sample movies data
INSERT INTO movies (title, release_year, genre, rating, rental_fee, available_copies, total_copies) VALUES
('The Shawshank Redemption', 1994, 'Drama', 9.3, 3.99, 3, 3),
('The Godfather', 1972, 'Crime', 9.2, 4.99, 2, 2),
('The Dark Knight', 2008, 'Action', 9.0, 3.99, 5, 5),
('Pulp Fiction', 1994, 'Crime', 8.9, 3.99, 2, 2),
('Inception', 2010, 'Sci-Fi', 8.8, 4.99, 4, 4),
('Forrest Gump', 1994, 'Drama', 8.8, 3.99, 3, 3),
('The Matrix', 1999, 'Sci-Fi', 8.7, 3.99, 4, 4),
('Goodfellas', 1990, 'Crime', 8.7, 3.99, 2, 2),
('The Lord of the Rings: The Fellowship of the Ring', 2001, 'Fantasy', 8.8, 4.99, 3, 3),
('Star Wars: Episode V - The Empire Strikes Back', 1980, 'Sci-Fi', 8.7, 3.99, 2, 2),
('Interstellar', 2014, 'Sci-Fi', 8.6, 4.99, 5, 5),
('Spirited Away', 2001, 'Animation', 8.6, 3.99, 2, 2),
('The Green Mile', 1999, 'Drama', 8.6, 3.99, 2, 2),
('Parasite', 2019, 'Thriller', 8.6, 4.99, 4, 4),
('The Lion King', 1994, 'Animation', 8.5, 2.99, 5, 5),
('Avengers: Endgame', 2019, 'Action', 8.4, 4.99, 6, 6),
('Back to the Future', 1985, 'Sci-Fi', 8.5, 3.99, 3, 3),
('Gladiator', 2000, 'Action', 8.5, 3.99, 3, 3),
('The Prestige', 2006, 'Mystery', 8.5, 3.99, 2, 2),
('The Departed', 2006, 'Crime', 8.5, 3.99, 2, 2),
('Whiplash', 2014, 'Drama', 8.5, 3.99, 2, 2),
('The Intouchables', 2011, 'Comedy', 8.5, 3.99, 2, 2),
('Modern Times', 1936, 'Comedy', 8.5, 2.99, 1, 1),
('City of God', 2002, 'Crime', 8.6, 3.99, 2, 2),
('Once Upon a Time in the West', 1968, 'Western', 8.5, 3.99, 1, 1);

-- Create indexes for better query performance
CREATE INDEX idx_movies_title ON movies(title);
CREATE INDEX idx_movies_genre ON movies(genre);
CREATE INDEX idx_movies_release_year ON movies(release_year);
CREATE INDEX idx_rentals_customer ON rentals(customer_id);
CREATE INDEX idx_rentals_status ON rentals(status);
CREATE INDEX idx_rental_items_movie ON rental_items(movie_id);

-- Display summary
SELECT 'Database setup complete!' AS status;
SELECT COUNT(*) AS total_movies FROM movies;
SELECT COUNT(*) AS total_customers FROM Customer;
SELECT COUNT(*) AS total_staff FROM staff;

-- Show sample movies
SELECT 
    title, 
    release_year, 
    genre, 
    rating, 
    CONCAT('$', rental_fee) AS rental_fee,
    available_copies
FROM movies
ORDER BY rating DESC
LIMIT 10;