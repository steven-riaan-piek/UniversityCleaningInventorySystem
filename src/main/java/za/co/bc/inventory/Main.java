package za.co.bc.inventory;

import za.co.bc.inventory.view.LoginForm;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Application entry point for the University Cleaning Inventory System.
 */
public final class Main {

    private Main() {
        // Prevent instantiation of this utility class.
    }

    public static void main(String[] args) {
        setSystemLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setLocationRelativeTo(null);
            loginForm.setVisible(true);
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            System.err.println("Could not apply the system look and feel: "
                    + exception.getMessage());
        }
    }
}
