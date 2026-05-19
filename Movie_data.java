package movieRental;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Movie_data {
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
    }

    public void addMovie(Movie movie) {
        String sql = "INSERT INTO movie (title, release_year, genre, rating, rental_fee) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movie.getTitle());
            stmt.setInt(2, movie.getReleaseYear());
            stmt.setString(3, movie.getGenre());
            stmt.setString(4, movie.getRating());  // Changed to setString
            stmt.setDouble(5, movie.getRentalFee());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMovie(String title, int releaseYear, String genre, String rating, double rentalFee, String oldTitle) {
        String sql = "UPDATE movie SET title=?, release_year=?, genre=?, rating=?, rental_fee=? WHERE title=?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setInt(2, releaseYear);
            stmt.setString(3, genre);
            stmt.setString(4, rating);  // Changed to setString
            stmt.setDouble(5, rentalFee);
            stmt.setString(6, oldTitle);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMovie(String title) {
        String sql = "DELETE FROM movie WHERE title=?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Movie> getAllMovies() {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM movie";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Movie(
                    rs.getString("title"),
                    rs.getInt("release_year"),
                    rs.getString("genre"),
                    rs.getString("rating"),  // Changed to getString
                    rs.getDouble("rental_fee")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Movie> searchMovies(String keyword) {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM movie WHERE title LIKE ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Movie(
                        rs.getString("title"),
                        rs.getInt("release_year"),
                        rs.getString("genre"),
                        rs.getString("rating"),  // Changed to getString
                        rs.getDouble("rental_fee")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
