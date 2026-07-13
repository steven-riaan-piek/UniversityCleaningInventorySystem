package za.co.bc.inventory.view;

import javax.swing.*;
import java.awt.*;

public class SupervisorDashboard extends JFrame {
    public SupervisorDashboard() {
        setTitle("Supervisor Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 330);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("Supervisor Dashboard", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JButton suppliers = new JButton("Manage Suppliers");
        JButton cleaners = new JButton("Manage Cleaners");
        JButton logout = new JButton("Logout");
        suppliers.addActionListener(e -> new SupplierForm().setVisible(true));
        cleaners.addActionListener(e -> new CleanerForm().setVisible(true));
        logout.addActionListener(e -> { new LoginForm().setVisible(true); dispose(); });

        JPanel actions = new JPanel(new GridLayout(0, 1, 10, 10));
        actions.setBorder(BorderFactory.createEmptyBorder(25, 90, 25, 90));
        actions.add(suppliers); actions.add(cleaners); actions.add(logout);
        add(title, BorderLayout.NORTH); add(actions, BorderLayout.CENTER);
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new SupervisorDashboard().setVisible(true)); }
}
