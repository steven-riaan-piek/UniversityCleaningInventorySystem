/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.co.bc.inventory.dao;

/**
 *
 * @author Jt
 */
import za.co.bc.inventory.database.DBConnection;
import za.co.bc.inventory.model.StockIssue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StockIssueDAO {

    public boolean issueStock(StockIssue issue) throws SQLException {
        Connection conn = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String updateQuery = "UPDATE Materials SET quantity_available = quantity_available - ? WHERE material_id = ? AND quantity_available >= ?";
            updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, issue.getQuantityIssued());
            updateStmt.setInt(2, issue.getMaterialId());
            updateStmt.setInt(3, issue.getQuantityIssued());

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Insufficient stock or invalid material ID.");
            }

            String insertQuery = "INSERT INTO StockIssuance (material_id, cleaner_id, quantity, issued_by_user, issue_date) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
            insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, issue.getMaterialId());
            insertStmt.setInt(2, issue.getCleanerId());
            insertStmt.setInt(3, issue.getQuantityIssued());
            insertStmt.setInt(4, issue.getIssuedByUserId());
            
            insertStmt.executeUpdate();

            conn.commit();
            success = true;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e; 
        } finally {
            if (updateStmt != null) updateStmt.close();
            if (insertStmt != null) insertStmt.close();
            if (conn != null) conn.setAutoCommit(true);
        }
        return success;
    }
}
