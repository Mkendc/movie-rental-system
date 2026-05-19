package movieRental;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CustomerDAO {

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
    }

    // 🟢 CREATE
    public void addCustomer(Customer c) {
        String sql = "INSERT INTO customers (first_name, last_name, email, phone, address, date_created, last_updated) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, c.getFirstName());
            pst.setString(2, c.getLastName());
            pst.setString(3, c.getEmail());
            pst.setString(4, c.getPhone());
            pst.setString(5, c.getAddress());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Customer added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding customer: " + e.getMessage());
        }
    }

    // 🟡 READ (All)
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_id DESC";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("customer_id"));
                c.setFirstName(rs.getString("first_name"));
                c.setLastName(rs.getString("last_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setAddress(rs.getString("address"));
                c.setDateCreated(rs.getTimestamp("date_created"));
                c.setLastUpdated(rs.getTimestamp("last_updated"));
                list.add(c);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving customers: " + e.getMessage());
        }
        return list;
    }

    // 🔵 READ (Search)
    public List<Customer> searchCustomer(String keyword) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR phone LIKE ?";
        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            String search = "%" + keyword + "%";
            pst.setString(1, search);
            pst.setString(2, search);
            pst.setString(3, search);
            pst.setString(4, search);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("customer_id"));
                c.setFirstName(rs.getString("first_name"));
                c.setLastName(rs.getString("last_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setAddress(rs.getString("address"));
                c.setDateCreated(rs.getTimestamp("date_created"));
                c.setLastUpdated(rs.getTimestamp("last_updated"));
                list.add(c);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error searching customers: " + e.getMessage());
        }
        return list;
    }

    // 🟠 UPDATE
    public void updateCustomer(Customer c) {
        String sql = "UPDATE customers SET first_name=?, last_name=?, email=?, phone=?, address=?, last_updated=NOW() WHERE customer_id=?";
        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, c.getFirstName());
            pst.setString(2, c.getLastName());
            pst.setString(3, c.getEmail());
            pst.setString(4, c.getPhone());
            pst.setString(5, c.getAddress());
            pst.setInt(6, c.getCustomerId());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Customer updated successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating customer: " + e.getMessage());
        }
    }

    // 🔴 DELETE
    public void deleteCustomer(int id) {
        String sql = "DELETE FROM customers WHERE customer_id=?";
        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Customer deleted successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting customer: " + e.getMessage());
        }
    }
}
