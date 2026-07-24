package za.co.bc.inventory.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import za.co.bc.inventory.database.DBConnection;
import za.co.bc.inventory.model.Material;

public class MaterialDAO {


     // Maps a database record to a Material object.

    private Material mapResultSetToMaterial(ResultSet rs) throws SQLException {

        Material material = new Material();

        material.setId(rs.getInt("id"));
        material.setName(rs.getString("name"));
        material.setDescription(rs.getString("description"));
        material.setCategory(rs.getString("category"));
        material.setQuantity(rs.getInt("quantity"));
        material.setUnit(rs.getString("unit"));
        material.setSuppliers(rs.getString("supplier"));
        material.setStatus(rs.getString("status"));

        return material;
    }


     // Retrieve all materials.

    public List<Material> getAllMaterials() {

        List<Material> materials = new ArrayList<>();

        String sql = "SELECT * FROM material ORDER BY id";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {
                materials.add(mapResultSetToMaterial(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return materials;
    }


     // Retrieve a single material by ID.

    public Material getMaterialById(int id) {

        String sql = "SELECT * FROM material WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return mapResultSetToMaterial(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


     // Insert a new material.

    public boolean addMaterial(Material material) {

        String sql = """
                INSERT INTO material
                (name, description, category, quantity, unit, supplier, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, material.getName());
            stmt.setString(2, material.getDescription());
            stmt.setString(3, material.getCategory());
            stmt.setInt(4, material.getQuantity());
            stmt.setString(5, material.getUnit());
            stmt.setString(6, material.getSuppliers());
            stmt.setString(7, material.getStatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


     // Update an existing material.

    public boolean updateMaterial(Material material) {

        String sql = """
                UPDATE material
                SET
                    name = ?,
                    description = ?,
                    category = ?,
                    quantity = ?,
                    unit = ?,
                    supplier = ?,
                    status = ?
                WHERE id = ?
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, material.getName());
            stmt.setString(2, material.getDescription());
            stmt.setString(3, material.getCategory());
            stmt.setInt(4, material.getQuantity());
            stmt.setString(5, material.getUnit());
            stmt.setString(6, material.getSuppliers());
            stmt.setString(7, material.getStatus());
            stmt.setInt(8, material.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


     // Delete a material.

    public boolean deleteMaterial(int id) {

        String sql = "DELETE FROM material WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


     //Search materials by name.

    public List<Material> searchMaterials(String keyword) {

        List<Material> materials = new ArrayList<>();

        String sql = "SELECT * FROM material WHERE name ILIKE ? ORDER BY name";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    materials.add(mapResultSetToMaterial(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return materials;
    }
}