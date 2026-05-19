package movieRental;

import java.sql.*;
import java.util.*;
import javax.swing.*;

public class StaffDAO {
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
    }

    // CREATE
    public void addStaff(String firstName, String lastName, String username, String password, String role) {
        String sql = "INSERT INTO staff (first_name, last_name, username, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, role);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Staff added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding staff: " + e.getMessage());
        }
    }

    // READ (SEARCH)
    public List<Staff> searchStaff(String keyword) {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE first_name LIKE ? OR last_name LIKE ? OR username LIKE ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Staff(
                    rs.getInt("staff_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("date_created"),
                    rs.getString("last_updated")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error searching staff: " + e.getMessage());
        }
        return list;
    }

    // UPDATE
    public void updateStaff(int id, String firstName, String lastName, String username, String password, String role) {
        String sql = "UPDATE staff SET first_name=?, last_name=?, username=?, password=?, role=? WHERE staff_id=?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, role);
            ps.setInt(6, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Staff updated successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating staff: " + e.getMessage());
        }
    }

    // DELETE
    public void deleteStaff(int id) {
        String sql = "DELETE FROM staff WHERE staff_id=?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Staff deleted successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting staff: " + e.getMessage());
        }
    }

    // SHOW ALL
    public List<Staff> getAllStaff() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staff";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Staff(
                    rs.getInt("staff_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("date_created"),
                    rs.getString("last_updated")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading staff: " + e.getMessage());
        }
        return list;
    }
    
    // AUTHENTICATION METHOD - For Login
    public Staff authenticate(String username, String password) {
        String sql = "SELECT * FROM staff WHERE username = ? AND password = ?";
        try (Connection conn = connect(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Staff(
                    rs.getInt("staff_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("date_created"),
                    rs.getString("last_updated")
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Login error: " + e.getMessage());
        }
        return null; // Return null if authentication fails
    }
}
