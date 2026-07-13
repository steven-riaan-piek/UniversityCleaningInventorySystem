package za.co.bc.inventory.dao;

import za.co.bc.inventory.database.DBConnection;
import za.co.bc.inventory.model.Cleaner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CleanerDAO {
    public boolean add(Cleaner cleaner) throws SQLException {
        String sql = "INSERT INTO cleaners (full_name, employee_number, phone, email, department) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = requireConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            setValues(stmt, cleaner);
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean update(Cleaner cleaner) throws SQLException {
        String sql = "UPDATE cleaners SET full_name=?, employee_number=?, phone=?, email=?, department=? WHERE id=?";
        try (Connection conn = requireConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            setValues(stmt, cleaner);
            stmt.setInt(6, cleaner.getId());
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection conn = requireConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM cleaners WHERE id=?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        }
    }

    public List<Cleaner> findAll() throws SQLException { return search(""); }

    public List<Cleaner> search(String term) throws SQLException {
        String sql = "SELECT id, full_name, employee_number, phone, email, department FROM cleaners " +
                "WHERE LOWER(full_name) LIKE ? OR LOWER(employee_number) LIKE ? OR LOWER(email) LIKE ? " +
                "OR LOWER(department) LIKE ? OR phone LIKE ? ORDER BY full_name";
        String value = "%" + (term == null ? "" : term.trim().toLowerCase()) + "%";
        List<Cleaner> cleaners = new ArrayList<>();
        try (Connection conn = requireConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) stmt.setString(i, value);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) cleaners.add(map(rs));
            }
        }
        return cleaners;
    }

    public boolean employeeNumberExists(String employeeNumber, Integer excludedId) throws SQLException {
        return valueExists("employee_number", employeeNumber, excludedId);
    }

    public boolean emailExists(String email, Integer excludedId) throws SQLException {
        return valueExists("email", email, excludedId);
    }

    private boolean valueExists(String column, String value, Integer excludedId) throws SQLException {
        String sql = "SELECT 1 FROM cleaners WHERE LOWER(" + column + ")=LOWER(?)" + (excludedId == null ? "" : " AND id<>?");
        try (Connection conn = requireConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            if (excludedId != null) stmt.setInt(2, excludedId);
            try (ResultSet rs = stmt.executeQuery()) { return rs.next(); }
        }
    }

    private static void setValues(PreparedStatement stmt, Cleaner cleaner) throws SQLException {
        stmt.setString(1, cleaner.getFullName());
        stmt.setString(2, cleaner.getEmployeeNumber());
        stmt.setString(3, cleaner.getPhone());
        stmt.setString(4, cleaner.getEmail());
        stmt.setString(5, cleaner.getDepartment());
    }

    private static Cleaner map(ResultSet rs) throws SQLException {
        return new Cleaner(rs.getInt("id"), rs.getString("full_name"), rs.getString("employee_number"),
                rs.getString("phone"), rs.getString("email"), rs.getString("department"));
    }

    private static Connection requireConnection() throws SQLException {
        Connection conn = DBConnection.getConnection();
        if (conn == null) throw new SQLException("Could not connect to the database. Check DBConnection settings and ensure PostgreSQL is running.");
        return conn;
    }
}
