-- =====================================================
-- Movie Rental System Database Setup Script
-- Complete Setup with All Tables and Dummy Data
-- =====================================================
-- 
-- LOGIN CREDENTIALS AFTER RUNNING THIS SCRIPT:
-- 
-- ADMIN ACCOUNTS (Full System Access):
--   Username: admin     Password: admin123
--   Username: gerald    Password: admin456
-- 
-- STAFF ACCOUNTS (Limited Access):
--   Username: john      Password: staff123
--   Username: jane      Password: staff456
--   Username: bob       Password: staff789
-- 
-- To run this script:
-- 1. Open MySQL Command Line or MySQL Workbench
-- 2. Run: source path/to/complete_database_setup.sql
-- =====================================================

-- Create database
DROP DATABASE IF EXISTS movie_rental_system;
CREATE DATABASE movie_rental_system;
USE movie_rental_system;

-- =====================================================
-- TABLE 1: CUSTOMERS
-- =====================================================
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert dummy customers
INSERT INTO customers (first_name, last_name, email, phone, address) VALUES
('Juan', 'Dela Cruz', 'juan.delacruz@email.com', '09171234567', '123 Rizal St, Manila'),
('Maria', 'Santos', 'maria.santos@email.com', '09182345678', '456 Bonifacio Ave, Quezon City'),
('Pedro', 'Garcia', 'pedro.garcia@email.com', '09193456789', '789 Mabini St, Makati'),
('Ana', 'Reyes', 'ana.reyes@email.com', '09204567890', '321 Luna St, Pasig'),
('Carlos', 'Fernandez', 'carlos.fernandez@email.com', '09215678901', '654 Del Pilar Ave, Mandaluyong'),
('Lisa', 'Mendoza', 'lisa.mendoza@email.com', '09226789012', '987 Aguinaldo St, Taguig'),
('Roberto', 'Cruz', 'roberto.cruz@email.com', '09237890123', '147 Quezon Blvd, Manila'),
('Elena', 'Aquino', 'elena.aquino@email.com', '09248901234', '258 Roxas Ave, Pasay'),
('Miguel', 'Torres', 'miguel.torres@email.com', '09259012345', '369 Laurel St, Caloocan'),
('Sofia', 'Ramos', 'sofia.ramos@email.com', '09260123456', '741 Osmena Blvd, Marikina'),
('Daniel', 'Lim', 'daniel.lim@email.com', '09271234567', '852 Recto Ave, Manila'),
('Grace', 'Tan', 'grace.tan@email.com', '09282345678', '963 Aurora Blvd, Quezon City'),
('James', 'Wong', 'james.wong@email.com', '09293456789', '159 Shaw Blvd, Mandaluyong'),
('Patricia', 'Lee', 'patricia.lee@email.com', '09304567890', '357 Ortigas Ave, Pasig'),
('Michael', 'Chen', 'michael.chen@email.com', '09315678901', '753 Ayala Ave, Makati');

-- =====================================================
-- TABLE 2: STAFF
-- =====================================================
CREATE TABLE staff (
    staff_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, -- Changed to VARCHAR to match Java code
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert dummy staff with correct login credentials
INSERT INTO staff (first_name, last_name, username, password, role) VALUES
-- ADMIN ACCOUNTS (Full System Access)
('Admin', 'User', 'admin', 'admin123', 'admin'),
('Gerald', 'Administrator', 'gerald', 'admin456', 'admin'),
-- STAFF ACCOUNTS (Limited Access - No Staff/Movie Management)
('John', 'Doe', 'john', 'staff123', 'staff'),
('Jane', 'Smith', 'jane', 'staff456', 'staff'),
('Bob', 'Wilson', 'bob', 'staff789', 'staff'),
('Sarah', 'Johnson', 'sarah', 'staff111', 'staff'),
('David', 'Brown', 'david', 'staff222', 'staff'),
('Emily', 'Davis', 'emily', 'staff333', 'staff');

-- =====================================================
-- TABLE 3: MOVIES
-- =====================================================
CREATE TABLE movie (
    movie_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    release_year INT,
    genre VARCHAR(100),
    rating VARCHAR(10),
    rental_fee DECIMAL(10,2),
    is_available BOOLEAN DEFAULT TRUE,
    stock_quantity INT DEFAULT 1,
    times_rented INT DEFAULT 0,
    image_path VARCHAR(500),
    description TEXT,
    director VARCHAR(150),
    duration_minutes INT,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_rating CHECK (rating IN ('G', 'PG', 'PG-13', 'R', 'NC-17'))
);

-- Insert dummy movies
INSERT INTO movie (title, release_year, genre, rating, rental_fee, is_available, stock_quantity, description, director, duration_minutes) VALUES
-- Action Movies
('Spider-Man: No Way Home', 2021, 'Action', 'PG-13', 150.00, TRUE, 5, 'Peter Parker seeks Doctor Stranges help to make people forget his identity', 'Jon Watts', 148),
('Top Gun: Maverick', 2022, 'Action', 'PG-13', 180.00, TRUE, 4, 'Pete Mitchell confronts his past while training Top Guns elite graduates', 'Joseph Kosinski', 131),
('The Batman', 2022, 'Action', 'PG-13', 160.00, TRUE, 3, 'Batman ventures into Gotham Citys underworld to unmask the Riddler', 'Matt Reeves', 176),
('Black Panther: Wakanda Forever', 2022, 'Action', 'PG-13', 170.00, TRUE, 4, 'The nation of Wakanda fights to protect their home from intervening world powers', 'Ryan Coogler', 161),
('John Wick: Chapter 4', 2023, 'Action', 'R', 200.00, TRUE, 3, 'John Wick uncovers a path to defeating The High Table', 'Chad Stahelski', 169),

-- Comedy Movies
('The Menu', 2022, 'Comedy', 'R', 120.00, TRUE, 3, 'A couple travels to a coastal island to eat at an exclusive restaurant', 'Mark Mylod', 107),
('Glass Onion', 2022, 'Comedy', 'PG-13', 140.00, TRUE, 2, 'Detective Benoit Blanc investigates a murder at a Greek island', 'Rian Johnson', 139),
('Barbie', 2023, 'Comedy', 'PG-13', 150.00, TRUE, 6, 'Barbie and Ken are having the time of their lives in Barbieland', 'Greta Gerwig', 114),

-- Drama Movies
('Everything Everywhere All at Once', 2022, 'Drama', 'R', 130.00, TRUE, 2, 'A woman gets caught in a multiverse adventure', 'Daniels', 139),
('The Whale', 2022, 'Drama', 'R', 110.00, TRUE, 2, 'A reclusive teacher attempts to reconnect with his estranged daughter', 'Darren Aronofsky', 117),
('Oppenheimer', 2023, 'Drama', 'R', 200.00, TRUE, 5, 'The story of J. Robert Oppenheimer and the Manhattan Project', 'Christopher Nolan', 180),

-- Horror Movies
('M3GAN', 2023, 'Horror', 'PG-13', 100.00, TRUE, 3, 'A robotics engineer creates a lifelike doll that becomes hostile', 'Gerard Johnstone', 102),
('Smile', 2022, 'Horror', 'R', 90.00, TRUE, 2, 'A doctor witnesses a traumatic incident involving a patient', 'Parker Finn', 115),
('The Black Phone', 2021, 'Horror', 'R', 85.00, TRUE, 2, 'A kidnapped boy finds a mysterious phone in his captors basement', 'Scott Derrickson', 103),

-- Animation Movies
('Turning Red', 2022, 'Animation', 'PG', 80.00, TRUE, 4, 'A 13-year-old girl turns into a giant red panda when excited', 'Domee Shi', 100),
('Encanto', 2021, 'Animation', 'PG', 85.00, TRUE, 5, 'A Colombian girl struggles with being the only non-magical member of her family', 'Byron Howard', 102),
('Elemental', 2023, 'Animation', 'PG', 95.00, TRUE, 3, 'Fire and water elements navigate a city where elements live together', 'Peter Sohn', 101),

-- Romance Movies
('The Notebook', 2004, 'Romance', 'PG-13', 60.00, TRUE, 3, 'A poor man falls in love with a rich woman in the 1940s', 'Nick Cassavetes', 123),
('Me Before You', 2016, 'Romance', 'PG-13', 70.00, TRUE, 2, 'A cheerful caregiver forms a bond with a paralyzed man', 'Thea Sharrock', 106),

-- Sci-Fi Movies
('Dune', 2021, 'Sci-Fi', 'PG-13', 160.00, TRUE, 3, 'Paul Atreides unites with the Fremen to prevent a terrible future', 'Denis Villeneuve', 155),
('Avatar: The Way of Water', 2022, 'Sci-Fi', 'PG-13', 250.00, TRUE, 4, 'Jake Sully lives with his family on Pandora and faces a returning threat', 'James Cameron', 192),

-- Filipino Movies
('Katips', 2021, 'Drama', 'PG', 100.00, TRUE, 2, 'Student activists during the Martial Law era in the Philippines', 'Vince Tanada', 142),
('Rewind', 2023, 'Drama', 'PG', 120.00, TRUE, 4, 'A man gets a chance to go back in time to save his wife', 'Mae Cruz-Alviar', 112),
('GomBurZa', 2023, 'Drama', 'PG-13', 110.00, TRUE, 2, 'The story of three Filipino priests executed in 1872', 'Pepe Diokno', 110),
('Hello, Love, Goodbye', 2019, 'Romance', 'PG', 90.00, TRUE, 3, 'An OFW in Hong Kong meets a bartender who changes her life', 'Cathy Garcia-Molina', 118),
('The Hows of Us', 2018, 'Romance', 'PG', 80.00, TRUE, 3, 'A couple struggles to keep their relationship and dreams alive', 'Cathy Garcia-Molina', 117);

-- =====================================================
-- TABLE 4: RENTALS (Main rental transactions)
-- =====================================================
CREATE TABLE rentals (
    rental_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    staff_id INT,
    rental_date DATE NOT NULL,
    return_date DATE NOT NULL,
    actual_return_date DATE,
    total_amount DECIMAL(10,2),
    deposit_amount DECIMAL(10,2) DEFAULT 0,
    balance_amount DECIMAL(10,2),
    late_fee DECIMAL(10,2) DEFAULT 0,
    status ENUM('Active', 'Returned', 'Overdue', 'Cancelled') DEFAULT 'Active',
    payment_status ENUM('Paid', 'Partial', 'Unpaid') DEFAULT 'Unpaid',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE SET NULL
);

-- Insert dummy rentals
INSERT INTO rentals (customer_id, staff_id, rental_date, return_date, actual_return_date, total_amount, deposit_amount, balance_amount, status, payment_status) VALUES
(1, 3, '2025-10-01', '2025-10-08', '2025-10-07', 450.00, 200.00, 250.00, 'Returned', 'Paid'),
(2, 3, '2025-10-02', '2025-10-09', '2025-10-09', 320.00, 150.00, 170.00, 'Returned', 'Paid'),
(3, 4, '2025-10-03', '2025-10-10', NULL, 280.00, 100.00, 180.00, 'Active', 'Partial'),
(4, 3, '2025-10-04', '2025-10-11', NULL, 200.00, 100.00, 100.00, 'Active', 'Partial'),
(5, 4, '2025-10-05', '2025-10-12', NULL, 360.00, 200.00, 160.00, 'Active', 'Partial'),
(6, 6, '2025-10-06', '2025-10-13', NULL, 150.00, 50.00, 100.00, 'Active', 'Partial'),
(7, 3, '2025-09-28', '2025-10-05', '2025-10-06', 420.00, 200.00, 220.00, 'Returned', 'Paid'),
(8, 4, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), NULL, 300.00, 150.00, 150.00, 'Active', 'Partial');

-- =====================================================
-- TABLE 5: RENTAL_ITEMS (Movies in each rental)
-- =====================================================
CREATE TABLE rental_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    rental_id INT NOT NULL,
    movie_id INT NOT NULL,
    rental_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (rental_id) REFERENCES rentals(rental_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movie(movie_id) ON DELETE CASCADE
);

-- Insert dummy rental items
INSERT INTO rental_items (rental_id, movie_id, rental_price) VALUES
-- Rental 1 (3 movies)
(1, 1, 150.00), (1, 2, 180.00), (1, 9, 130.00),
-- Rental 2 (2 movies)
(2, 3, 160.00), (2, 4, 170.00),
-- Rental 3 (2 movies)
(3, 5, 200.00), (3, 15, 80.00),
-- Rental 4 (2 movies)
(4, 12, 100.00), (4, 13, 90.00),
-- Rental 5 (3 movies)
(5, 6, 120.00), (5, 7, 140.00), (5, 16, 85.00),
-- Rental 6 (1 movie)
(6, 11, 200.00),
-- Rental 7 (3 movies)
(7, 19, 160.00), (7, 20, 250.00), (7, 18, 60.00),
-- Rental 8 (2 movies)
(8, 22, 120.00), (8, 23, 110.00);

-- =====================================================
-- TABLE 6: PAYMENTS
-- =====================================================
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    rental_id INT,
    customer_id INT,
    staff_id INT,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('Cash', 'Credit Card', 'Debit Card', 'GCash', 'PayMaya') DEFAULT 'Cash',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rental_id) REFERENCES rentals(rental_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE SET NULL
);

-- Insert dummy payments
INSERT INTO payments (rental_id, customer_id, staff_id, amount, payment_method) VALUES
(1, 1, 3, 200.00, 'Cash'),
(1, 1, 3, 250.00, 'GCash'),
(2, 2, 3, 320.00, 'Credit Card'),
(3, 3, 4, 100.00, 'Cash'),
(4, 4, 3, 100.00, 'PayMaya'),
(5, 5, 4, 200.00, 'Debit Card'),
(6, 6, 6, 50.00, 'Cash'),
(7, 7, 3, 420.00, 'GCash');

-- =====================================================
-- VIEWS FOR REPORTING
-- =====================================================

-- View for available movies
CREATE VIEW available_movies AS
SELECT movie_id, title, release_year, genre, rating, rental_fee, stock_quantity
FROM movie
WHERE is_available = TRUE AND stock_quantity > 0
ORDER BY title;

-- View for active rentals
CREATE VIEW active_rentals AS
SELECT 
    r.rental_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    c.phone,
    r.rental_date,
    r.return_date,
    r.total_amount,
    r.balance_amount,
    COUNT(ri.item_id) AS movies_rented
FROM rentals r
JOIN customers c ON r.customer_id = c.customer_id
LEFT JOIN rental_items ri ON r.rental_id = ri.rental_id
WHERE r.status = 'Active'
GROUP BY r.rental_id
ORDER BY r.return_date;

-- View for overdue rentals
CREATE VIEW overdue_rentals AS
SELECT 
    r.rental_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    c.phone,
    c.email,
    r.rental_date,
    r.return_date,
    DATEDIFF(CURDATE(), r.return_date) AS days_overdue,
    r.total_amount,
    r.balance_amount
FROM rentals r
JOIN customers c ON r.customer_id = c.customer_id
WHERE r.status = 'Active' AND r.return_date < CURDATE()
ORDER BY days_overdue DESC;

-- View for popular movies
CREATE VIEW popular_movies AS
SELECT 
    m.movie_id,
    m.title,
    m.genre,
    m.rating,
    m.rental_fee,
    COUNT(ri.item_id) AS total_rentals
FROM movie m
LEFT JOIN rental_items ri ON m.movie_id = ri.movie_id
GROUP BY m.movie_id
ORDER BY total_rentals DESC
LIMIT 10;

-- View for customer rental history
CREATE VIEW customer_rental_history AS
SELECT 
    c.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    COUNT(DISTINCT r.rental_id) AS total_rentals,
    SUM(r.total_amount) AS total_spent,
    MAX(r.rental_date) AS last_rental_date
FROM customers c
LEFT JOIN rentals r ON c.customer_id = r.customer_id
GROUP BY c.customer_id
ORDER BY total_spent DESC;

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================
CREATE INDEX idx_movie_title ON movie(title);
CREATE INDEX idx_movie_genre ON movie(genre);
CREATE INDEX idx_movie_year ON movie(release_year);
CREATE INDEX idx_customer_email ON customers(email);
CREATE INDEX idx_customer_phone ON customers(phone);
CREATE INDEX idx_rental_status ON rentals(status);
CREATE INDEX idx_rental_dates ON rentals(rental_date, return_date);
CREATE INDEX idx_staff_username ON staff(username);

-- =====================================================
-- STORED PROCEDURES
-- =====================================================

-- Procedure to calculate late fees
DELIMITER //
CREATE PROCEDURE CalculateLateFee(IN rental_id INT)
BEGIN
    DECLARE days_late INT;
    DECLARE late_fee_per_day DECIMAL(10,2) DEFAULT 20.00;
    DECLARE total_late_fee DECIMAL(10,2);
    
    SELECT DATEDIFF(CURDATE(), return_date) INTO days_late
    FROM rentals
    WHERE rental_id = rental_id AND status = 'Active' AND return_date < CURDATE();
    
    IF days_late > 0 THEN
        SET total_late_fee = days_late * late_fee_per_day;
        UPDATE rentals 
        SET late_fee = total_late_fee, status = 'Overdue'
        WHERE rental_id = rental_id;
    END IF;
END //
DELIMITER ;

-- Procedure to process a return
DELIMITER //
CREATE PROCEDURE ProcessReturn(IN p_rental_id INT)
BEGIN
    UPDATE rentals 
    SET 
        actual_return_date = CURDATE(),
        status = 'Returned'
    WHERE rental_id = p_rental_id;
    
    -- Calculate any late fees
    CALL CalculateLateFee(p_rental_id);
END //
DELIMITER ;

-- =====================================================
-- TRIGGERS
-- =====================================================

-- Update movie availability when rented
DELIMITER //
CREATE TRIGGER after_rental_insert
AFTER INSERT ON rental_items
FOR EACH ROW
BEGIN
    UPDATE movie 
    SET 
        stock_quantity = stock_quantity - 1,
        times_rented = times_rented + 1,
        is_available = CASE WHEN stock_quantity <= 1 THEN FALSE ELSE TRUE END
    WHERE movie_id = NEW.movie_id;
END //
DELIMITER ;

-- Update movie availability when returned
DELIMITER //
CREATE TRIGGER after_rental_return
AFTER UPDATE ON rentals
FOR EACH ROW
BEGIN
    IF NEW.status = 'Returned' AND OLD.status != 'Returned' THEN
        UPDATE movie m
        INNER JOIN rental_items ri ON m.movie_id = ri.movie_id
        SET 
            m.stock_quantity = m.stock_quantity + 1,
            m.is_available = TRUE
        WHERE ri.rental_id = NEW.rental_id;
    END IF;
END //
DELIMITER ;

-- =====================================================
-- DISPLAY SUMMARY
-- =====================================================
SELECT 'Database Setup Complete!' AS Status;

-- Show statistics
SELECT 
    (SELECT COUNT(*) FROM customers) AS Total_Customers,
    (SELECT COUNT(*) FROM staff) AS Total_Staff,
    (SELECT COUNT(*) FROM movie) AS Total_Movies,
    (SELECT COUNT(*) FROM rentals) AS Total_Rentals,
    (SELECT COUNT(*) FROM rentals WHERE status = 'Active') AS Active_Rentals,
    (SELECT SUM(total_amount) FROM rentals) AS Total_Revenue;

-- Show sample data
SELECT '\n=== Sample Movies ===' AS '';
SELECT movie_id, title, genre, rating, CONCAT('₱', rental_fee) AS rental_fee, 
       CASE WHEN is_available THEN 'Yes' ELSE 'No' END AS available 
FROM movie LIMIT 10;

SELECT '\n=== Sample Customers ===' AS '';
SELECT customer_id, CONCAT(first_name, ' ', last_name) AS name, email, phone 
FROM customers LIMIT 5;

SELECT '\n=== Active Rentals ===' AS '';
SELECT * FROM active_rentals;