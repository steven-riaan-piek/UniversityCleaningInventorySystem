package za.co.bc.inventory.dao;

import za.co.bc.inventory.database.DBConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class ReportDAO {

    public int getTotalMaterialsCount() {
        return getCount("SELECT COUNT(*) FROM material");
    }

    public int getTotalCleanersCount() {
        return getCount("SELECT COUNT(*) FROM cleaners");
    }

    private int getCount(String query) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getLowStockItemsCount() {
        String query = "SELECT COUNT(*) FROM material WHERE quantity <= reorder_level";
        return getCount(query);
    }

    public DefaultTableModel getRecentIssuancesModel() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Material");
        columnNames.add("Cleaner");
        columnNames.add("Quantity");
        columnNames.add("Date");

        Vector<Vector<Object>> data = new Vector<>();

        String query = """
                SELECT m.name AS material_name,
                       c.full_name AS cleaner_name,
                       s.quantity,
                       s.issue_date
                FROM stock_issuance s
                JOIN material m ON s.material_id = m.id
                JOIN cleaners c ON s.cleaner_id = c.id
                ORDER BY s.issue_date DESC
                LIMIT 10
                """;

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

        return new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
