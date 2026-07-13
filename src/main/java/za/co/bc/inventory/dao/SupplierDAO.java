package za.co.bc.inventory.dao;

import za.co.bc.inventory.database.DBConnection;
import za.co.bc.inventory.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    public boolean add(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = requireConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            setValues(stmt, supplier);
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean update(Supplier supplier) throws SQLException {
        String sql = "UPDATE suppliers SET name=?, contact_person=?, phone=?, email=?, address=? WHERE id=?";
        try (Connection conn = requireConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            setValues(stmt, supplier);
            stmt.setInt(6, supplier.getId());
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection conn = requireConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM suppliers WHERE id=?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        }
    }

    public List<Supplier> findAll() throws SQLException { return search(""); }

    public List<Supplier> search(String term) throws SQLException {
        String sql = "SELECT id, name, contact_person, phone, email, address FROM suppliers " +
                "WHERE LOWER(name) LIKE ? OR LOWER(contact_person) LIKE ? OR LOWER(email) LIKE ? OR phone LIKE ? ORDER BY name";
        String value = "%" + (term == null ? "" : term.trim().toLowerCase()) + "%";
        List<Supplier> suppliers = new ArrayList<>();
        try (Connection conn = requireConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) stmt.setString(i, value);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) suppliers.add(map(rs));
            }
        }
        return suppliers;
    }

    public boolean emailExists(String email, Integer excludedId) throws SQLException {
        String sql = "SELECT 1 FROM suppliers WHERE LOWER(email)=LOWER(?)" + (excludedId == null ? "" : " AND id<>?");
        try (Connection conn = requireConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            if (excludedId != null) stmt.setInt(2, excludedId);
            try (ResultSet rs = stmt.executeQuery()) { return rs.next(); }
        }
    }

    private static void setValues(PreparedStatement stmt, Supplier supplier) throws SQLException {
        stmt.setString(1, supplier.getName());
        stmt.setString(2, supplier.getContactPerson());
        stmt.setString(3, supplier.getPhone());
        stmt.setString(4, supplier.getEmail());
        stmt.setString(5, supplier.getAddress());
    }

    private static Supplier map(ResultSet rs) throws SQLException {
        return new Supplier(rs.getInt("id"), rs.getString("name"), rs.getString("contact_person"),
                rs.getString("phone"), rs.getString("email"), rs.getString("address"));
    }

    private static Connection requireConnection() throws SQLException {
        Connection conn = DBConnection.getConnection();
        if (conn == null) throw new SQLException("Could not connect to the database. Check DBConnection settings and ensure PostgreSQL is running.");
        return conn;
    }
}
