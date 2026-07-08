package za.co.bc.inventory.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import za.co.bc.inventory.database.DBConnection;
import za.co.bc.inventory.model.User;

public class UserDAO {

    public boolean registerUser(User user) {

        String sql = "INSERT INTO users(full_name, username, email, password, role) VALUES (?, ?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRole());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean usernameExists(String username) {

        String sql = "SELECT * FROM users WHERE username = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean emailExists(String email) {

        String sql = "SELECT * FROM users WHERE email = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public User login(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                User user = new User();

                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));

                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}