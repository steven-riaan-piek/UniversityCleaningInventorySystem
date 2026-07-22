package za.co.bc.inventory.view;

import za.co.bc.inventory.dao.ReportDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main dashboard for storekeepers.
 * Provides navigation to day-to-day inventory management modules.
 */
public class StorekeeperDashboard extends JFrame {

    private final ReportDAO reportDAO = new ReportDAO();

    private JLabel lblTotalMaterials;
    private JLabel lblTotalCleaners;
    private JLabel lblLowStock;
    private JTable tblRecentIssuances;

    public StorekeeperDashboard() {
        initialiseWindow();
        buildInterface();
        loadDashboardData();
    }

    private void initialiseWindow() {
        setTitle("Storekeeper Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 520));
        setLocationRelativeTo(null);
    }

    private void buildInterface() {
        setLayout(new BorderLayout(12, 12));

        JLabel title = new JLabel("Storekeeper Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(12, 10, 0, 10));
        add(title, BorderLayout.NORTH);

        JPanel navigation = new JPanel(new GridLayout(0, 1, 8, 8));
        navigation.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 5));

        JButton btnMaterials = new JButton("Manage Materials");
        JButton btnSuppliers = new JButton("Manage Suppliers");
        JButton btnCleaners = new JButton("Manage Cleaners");
        JButton btnStockIssuance = new JButton("Stock Issuance");
        JButton btnRefresh = new JButton("Refresh Dashboard");
        JButton btnLogout = new JButton("Log Out");

        navigation.add(btnMaterials);
        navigation.add(btnSuppliers);
        navigation.add(btnCleaners);
        navigation.add(btnStockIssuance);
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

        add(content, BorderLayout.CENTER);

        btnMaterials.addActionListener(e -> openChildWindow(new MaterialForm()));
        btnSuppliers.addActionListener(e -> openChildWindow(new SupplierForm()));
        btnCleaners.addActionListener(e -> openChildWindow(new CleanerForm()));
        btnStockIssuance.addActionListener(e -> openChildWindow(new StockIssueForm()));
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
        SwingUtilities.invokeLater(() -> new StorekeeperDashboard().setVisible(true));
    }
}
