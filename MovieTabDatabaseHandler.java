package movieRental;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database handler specifically for movieTab.java
 * This class connects the movieTab GUI to the movie_rental_system database
 */
public class MovieTabDatabaseHandler {
    
    // ======================== MOVIE OPERATIONS ========================
    
    /**
     * Load all movies from database
     */
    public List<Object[]> getAllMovies() {
        List<Object[]> movies = new ArrayList<>();
        String sql = "SELECT movie_id, title, release_year, genre, rating, rental_fee FROM movies ORDER BY title";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getInt("release_year"),
                    rs.getString("genre"),
                    rs.getDouble("rating"),
                    rs.getDouble("rental_fee")
                };
                movies.add(row);
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error loading movies: " + e.getMessage());
            e.printStackTrace();
        }
        
        return movies;
    }
    
    /**
     * Search movies by title, genre, or year
     */
    public List<Object[]> searchMovies(String searchTerm) {
        List<Object[]> movies = new ArrayList<>();
        String sql = "SELECT movie_id, title, release_year, genre, rating, rental_fee " +
                    "FROM movies WHERE title LIKE ? OR genre LIKE ? OR release_year LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getInt("release_year"),
                    rs.getString("genre"),
                    rs.getDouble("rating"),
                    rs.getDouble("rental_fee")
                };
                movies.add(row);
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error searching movies: " + e.getMessage());
            e.printStackTrace();
        }
        
        return movies;
    }
    
    /**
     * Add a new movie to database
     */
    public boolean addMovie(String title, int releaseYear, String genre, double rating, double rentalFee) {
        String sql = "INSERT INTO movies (title, release_year, genre, rating, rental_fee, available_copies, total_copies) " +
                    "VALUES (?, ?, ?, ?, ?, 1, 1)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, title);
            stmt.setInt(2, releaseYear);
            stmt.setString(3, genre);
            stmt.setDouble(4, rating);
            stmt.setDouble(5, rentalFee);
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                showSuccess("Movie added successfully!");
                return true;
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error adding movie: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update existing movie
     */
    public boolean updateMovie(int movieId, String title, int releaseYear, String genre, double rating, double rentalFee) {
        String sql = "UPDATE movies SET title=?, release_year=?, genre=?, rating=?, rental_fee=? WHERE movie_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, title);
            stmt.setInt(2, releaseYear);
            stmt.setString(3, genre);
            stmt.setDouble(4, rating);
            stmt.setDouble(5, rentalFee);
            stmt.setInt(6, movieId);
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                showSuccess("Movie updated successfully!");
                return true;
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error updating movie: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete movie from database
     */
    public boolean deleteMovie(int movieId) {
        // First check if movie is currently rented
        if (isMovieRented(movieId)) {
            showError("Cannot Delete", "This movie is currently rented and cannot be deleted.");
            return false;
        }
        
        String sql = "DELETE FROM movies WHERE movie_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, movieId);
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                showSuccess("Movie deleted successfully!");
                return true;
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error deleting movie: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Check if movie is currently rented
     */
    private boolean isMovieRented(int movieId) {
        String sql = "SELECT COUNT(*) FROM rental_items ri " +
                    "JOIN rentals r ON ri.rental_id = r.rental_id " +
                    "WHERE ri.movie_id = ? AND r.returned_date IS NULL";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ======================== CUSTOMER OPERATIONS ========================
    
    /**
     * Load all customers from database
     */
    public List<Object[]> getAllCustomers() {
        List<Object[]> customers = new ArrayList<>();
        String sql = "SELECT customer_id, first_name, last_name, email, phone, address FROM Customer ORDER BY first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("customer_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address")
                };
                customers.add(row);
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error loading customers: " + e.getMessage());
            e.printStackTrace();
        }
        
        return customers;
    }
    
    /**
     * Search customers by ID or name
     */
    public List<Object[]> searchCustomers(String searchTerm) {
        List<Object[]> customers = new ArrayList<>();
        String sql = "SELECT customer_id, first_name, last_name, email, phone, address " +
                    "FROM Customer WHERE customer_id = ? OR first_name LIKE ? OR last_name LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Try to parse as ID
            try {
                int id = Integer.parseInt(searchTerm);
                stmt.setInt(1, id);
            } catch (NumberFormatException e) {
                stmt.setInt(1, -1); // No match
            }
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("customer_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address")
                };
                customers.add(row);
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error searching customers: " + e.getMessage());
            e.printStackTrace();
        }
        
        return customers;
    }
    
    /**
     * Add new customer
     */
    public boolean addCustomer(String firstName, String lastName, String email, String phone, String address) {
        String sql = "INSERT INTO Customer (first_name, last_name, email, phone, address, date_created) " +
                    "VALUES (?, ?, ?, ?, ?, NOW())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                showSuccess("Customer added successfully!");
                return true;
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error adding customer: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update customer information
     */
    public boolean updateCustomer(int customerId, String firstName, String lastName, String email, String phone, String address) {
        String sql = "UPDATE Customer SET first_name=?, last_name=?, email=?, phone=?, address=? WHERE customer_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setInt(6, customerId);
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                showSuccess("Customer updated successfully!");
                return true;
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error updating customer: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete customer
     */
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM Customer WHERE customer_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                showSuccess("Customer deleted successfully!");
                return true;
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error deleting customer: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ======================== STAFF OPERATIONS ========================
    
    /**
     * Load all staff from database
     */
    public List<Object[]> getAllStaff() {
        List<Object[]> staffList = new ArrayList<>();
        String sql = "SELECT staff_id, first_name, last_name, username, role, status, date_created, last_updated " +
                    "FROM staff ORDER BY first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("staff_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    "******", // Don't show password
                    rs.getString("role"),
                    rs.getString("date_created"),
                    rs.getString("last_updated")
                };
                staffList.add(row);
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error loading staff: " + e.getMessage());
            e.printStackTrace();
        }
        
        return staffList;
    }
    
    /**
     * Search staff by name or username
     */
    public List<Object[]> searchStaff(String searchTerm) {
        List<Object[]> staffList = new ArrayList<>();
        String sql = "SELECT staff_id, first_name, last_name, username, role, status, date_created, last_updated " +
                    "FROM staff WHERE first_name LIKE ? OR last_name LIKE ? OR username LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("staff_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    "******",
                    rs.getString("role"),
                    rs.getString("date_created"),
                    rs.getString("last_updated")
                };
                staffList.add(row);
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error searching staff: " + e.getMessage());
            e.printStackTrace();
        }
        
        return staffList;
    }
    
    /**
     * Add new staff member
     */
    public boolean addStaff(String firstName, String lastName, String email, String phone, 
                           String username, String password, String role, String status) {
        String sql = "INSERT INTO staff (first_name, last_name, email, phone, username, password, role, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, username);
            stmt.setString(6, password); // In production, hash this!
            stmt.setString(7, role);
            stmt.setString(8, status);
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                showSuccess("Staff member added successfully!");
                return true;
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error adding staff: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete staff member
     */
    public boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM staff WHERE staff_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, staffId);
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                showSuccess("Staff member deleted successfully!");
                return true;
            }
            
        } catch (SQLException e) {
            showError("Database Error", "Error deleting staff: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ======================== TABLE POPULATION METHODS ========================
    
    /**
     * Populate JTable with movie data
     */
    public void populateMovieTable(JTable table, List<Object[]> data) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing rows
        
        for (Object[] row : data) {
            model.addRow(row);
        }
    }
    
    /**
     * Populate JTable with customer data
     */
    public void populateCustomerTable(JTable table, List<Object[]> data) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing rows
        
        for (Object[] row : data) {
            model.addRow(row);
        }
    }
    
    /**
     * Populate JTable with staff data
     */
    public void populateStaffTable(JTable table, List<Object[]> data) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing rows
        
        for (Object[] row : data) {
            model.addRow(row);
        }
    }
    
    // ======================== UTILITY METHODS ========================
    
    /**
     * Test database connection
     */
    public boolean testConnection() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                showSuccess("Database connection successful!");
                return true;
            }
        } catch (SQLException e) {
            showError("Connection Failed", "Could not connect to database: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get movie by ID
     */
    public Object[] getMovieById(int movieId) {
        String sql = "SELECT movie_id, title, release_year, genre, rating, rental_fee FROM movies WHERE movie_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Object[]{
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getInt("release_year"),
                    rs.getString("genre"),
                    rs.getDouble("rating"),
                    rs.getDouble("rental_fee")
                };
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get customer by ID
     */
    public Object[] getCustomerById(int customerId) {
        String sql = "SELECT customer_id, first_name, last_name, email, phone, address FROM Customer WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Object[]{
                    rs.getInt("customer_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address")
                };
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Show error dialog
     */
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Show success dialog
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show warning dialog
     */
    public void showWarning(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }
}