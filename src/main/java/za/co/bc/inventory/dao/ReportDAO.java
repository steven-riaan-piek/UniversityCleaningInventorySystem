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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class ReportDAO {

    public int getTotalCount(String tableName) {
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + tableName;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
             
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public int getLowStockItemsCount() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM Materials WHERE quantity_available <= reorder_level";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
             
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public DefaultTableModel getRecentIssuancesModel() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Material");
        columnNames.add("Cleaner");
        columnNames.add("Quantity");
        columnNames.add("Date");

        Vector<Vector<Object>> data = new Vector<>();
        
        // JOIN query to get human-readable names instead of IDs
        String query = "SELECT m.name AS material_name, c.first_name || ' ' || c.last_name AS cleaner_name, s.quantity, s.issue_date " +
                       "FROM StockIssuance s " +
                       "JOIN Materials m ON s.material_id = m.material_id " +
                       "JOIN Cleaners c ON s.cleaner_id = c.cleaner_id " +
                       "ORDER BY s.issue_date DESC LIMIT 10";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("material_name"));
                row.add(rs.getString("cleaner_name"));
                row.add(rs.getInt("quantity"));
                row.add(rs.getTimestamp("issue_date"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new DefaultTableModel(data, columnNames);
    }
}