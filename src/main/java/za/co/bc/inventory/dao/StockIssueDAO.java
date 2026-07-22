package za.co.bc.inventory.dao;

import za.co.bc.inventory.database.DBConnection;
import za.co.bc.inventory.model.StockIssue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StockIssueDAO {

    public boolean issueStock(StockIssue issue) throws SQLException {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Could not connect to the database.");
        }

        try {
            conn.setAutoCommit(false);

            String updateQuery = """
                    UPDATE material
                    SET quantity = quantity - ?
                    WHERE id = ? AND quantity >= ?
                    """;

            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, issue.getQuantityIssued());
                updateStmt.setInt(2, issue.getMaterialId());
                updateStmt.setInt(3, issue.getQuantityIssued());

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Insufficient stock or invalid material ID.");
                }
            }

            String insertQuery = """
                    INSERT INTO stock_issuance
                        (material_id, cleaner_id, quantity, issued_by_user, issue_date)
                    VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
                    """;

            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, issue.getMaterialId());
                insertStmt.setInt(2, issue.getCleanerId());
                insertStmt.setInt(3, issue.getQuantityIssued());
                insertStmt.setInt(4, issue.getIssuedByUserId());
                insertStmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackError) {
                e.addSuppressed(rollbackError);
            }
            throw e;
        } finally {
            try {
                conn.setAutoCommit(true);
            } finally {
                conn.close();
            }
        }
    }
}
