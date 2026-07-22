package za.co.bc.inventory.view;

import za.co.bc.inventory.dao.ReportDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Main dashboard for supervisors.
 * Provides navigation to all management modules and reports.
 */
public class SupervisorDashboard extends JFrame {

    private final ReportDAO reportDAO = new ReportDAO();

    private JLabel lblTotalMaterials;
    private JLabel lblTotalCleaners;
    private JLabel lblLowStock;
    private JTable tblRecentIssuances;

    public SupervisorDashboard() {
        initialiseWindow();
        buildInterface();
        loadDashboardData();
    }

    private void initialiseWindow() {
        setTitle("Supervisor Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 520));
        setLocationRelativeTo(null);
    }

    private void buildInterface() {
        setLayout(new BorderLayout(12, 12));

        JLabel title = new JLabel("Supervisor Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(12, 10, 0, 10));
        add(title, BorderLayout.NORTH);

        JPanel navigation = new JPanel(new GridLayout(0, 1, 8, 8));
        navigation.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 5));

        JButton btnMaterials = new JButton("Manage Materials");
        JButton btnSuppliers = new JButton("Manage Suppliers");
        JButton btnCleaners = new JButton("Manage Cleaners");
        JButton btnStockIssuance = new JButton("Stock Issuance");
        JButton btnReports = new JButton("View Full Report");
        JButton btnRefresh = new JButton("Refresh Dashboard");
        JButton btnLogout = new JButton("Log Out");

        navigation.add(btnMaterials);
        navigation.add(btnSuppliers);
        navigation.add(btnCleaners);
        navigation.add(btnStockIssuance);
        navigation.add(btnReports);
        navigation.add(btnRefresh);
        navigation.add(btnLogout);
        add(navigation, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 15));

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 5));
        lblTotalMaterials = new JLabel("Total Materials: 0");
        lblTotalCleaners = new JLabel("Total Cleaners: 0");
        lblLowStock = new JLabel("Low Stock: 0");
        stats.add(lblTotalMaterials);
        stats.add(lblTotalCleaners);
        stats.add(lblLowStock);
        content.add(stats, BorderLayout.NORTH);

        tblRecentIssuances = new JTable();
        tblRecentIssuances.setFillsViewportHeight(true);
        content.add(new JScrollPane(tblRecentIssuances), BorderLayout.CENTER);

        JLabel recentLabel = new JLabel("Recent Stock Issuances");
        recentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.add(recentLabel);
        content.add(south, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);

        btnMaterials.addActionListener(e -> openChildWindow(new MaterialForm()));
        btnSuppliers.addActionListener(e -> openChildWindow(new SupplierForm()));
        btnCleaners.addActionListener(e -> openChildWindow(new CleanerForm()));
        btnStockIssuance.addActionListener(e -> openChildWindow(new StockIssueForm()));
        btnReports.addActionListener(e -> showLowStockReport());
        btnRefresh.addActionListener(e -> loadDashboardData());
        btnLogout.addActionListener(e -> logout());
    }

    private void openChildWindow(JFrame form) {
        form.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        form.setLocationRelativeTo(this);
        form.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadDashboardData();
            }
        });
        form.setVisible(true);
    }

    private void loadDashboardData() {
        lblTotalMaterials.setText("Total Materials: " + reportDAO.getTotalMaterialsCount());
        lblTotalCleaners.setText("Total Cleaners: " + reportDAO.getTotalCleanersCount());
        lblLowStock.setText("Low Stock: " + reportDAO.getLowStockItemsCount());
        tblRecentIssuances.setModel(reportDAO.getRecentIssuancesModel());
    }

    private void showLowStockReport() {
        StringBuilder report = new StringBuilder("=== LOW-STOCK ALERT REPORT ===\n\n");
        String query = "SELECT name, quantity, reorder_level FROM material " +
                "WHERE quantity <= reorder_level ORDER BY quantity ASC, name";

        try (Connection conn = za.co.bc.inventory.database.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            boolean found = false;
            while (rs.next()) {
                found = true;
                report.append("• ").append(rs.getString("name"))
                        .append(" | Current Stock: ").append(rs.getInt("quantity"))
                        .append(" | Reorder Level: ").append(rs.getInt("reorder_level"))
                        .append('\n');
            }

            if (!found) {
                report.append("All inventory levels are currently normal.");
            }

            JOptionPane.showMessageDialog(
                    this,
                    report.toString(),
                    "Inventory Status Report",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error fetching report: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Log Out",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            LoginForm loginForm = new LoginForm();
            loginForm.setLocationRelativeTo(null);
            loginForm.setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SupervisorDashboard().setVisible(true));
    }
}
