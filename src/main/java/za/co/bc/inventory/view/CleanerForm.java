package za.co.bc.inventory.view;

import za.co.bc.inventory.dao.CleanerDAO;
import za.co.bc.inventory.model.Cleaner;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class CleanerForm extends JFrame {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+() -]{7,25}$");

    private final CleanerDAO cleanerDAO = new CleanerDAO();
    private final JTextField txtFullName = new JTextField();
    private final JTextField txtEmployeeNumber = new JTextField();
    private final JTextField txtPhone = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JComboBox<String> cmbDepartment = new JComboBox<>(new String[]{
            "Administration", "Engineering", "IT", "Library", "Residences", "Science", "Sports", "Other"
    });
    private final JTextField txtSearch = new JTextField(22);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Full Name", "Employee No.", "Phone", "Email", "Department"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private Integer selectedId;

    public CleanerForm() {
        setTitle("Cleaner Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        buildUi();
        loadCleaners("");
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel title = new JLabel("Cleaner Management");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSearch = new JButton("Search");
        JButton btnShowAll = new JButton("Show All");
        searchPanel.add(new JLabel("Search:")); searchPanel.add(txtSearch); searchPanel.add(btnSearch); searchPanel.add(btnShowAll);
        JPanel top = new JPanel(new BorderLayout()); top.add(title, BorderLayout.WEST); top.add(searchPanel, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Cleaner Details"));
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(6, 6, 6, 6); c.fill = GridBagConstraints.HORIZONTAL;
        addField(form, c, 0, "Full name*", txtFullName);
        addField(form, c, 1, "Employee number*", txtEmployeeNumber);
        addField(form, c, 2, "Phone*", txtPhone);
        addField(form, c, 3, "Email*", txtEmail);
        cmbDepartment.setEditable(true);
        addField(form, c, 4, "Department*", cmbDepartment);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add"); JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete"); JButton btnClear = new JButton("Clear");
        buttons.add(btnAdd); buttons.add(btnUpdate); buttons.add(btnDelete); buttons.add(btnClear);
        c.gridx = 0; c.gridy = 5; c.gridwidth = 2; c.weightx = 1; form.add(buttons, c);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); table.setAutoCreateRowSorter(true);
        JScrollPane tableScroll = new JScrollPane(table); tableScroll.setBorder(BorderFactory.createTitledBorder("Cleaners"));
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, tableScroll); split.setResizeWeight(0.34);
        root.add(split, BorderLayout.CENTER); setContentPane(root);

        btnAdd.addActionListener(e -> addCleaner()); btnUpdate.addActionListener(e -> updateCleaner());
        btnDelete.addActionListener(e -> deleteCleaner()); btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> loadCleaners(txtSearch.getText()));
        btnShowAll.addActionListener(e -> { txtSearch.setText(""); loadCleaners(""); });
        txtSearch.addActionListener(e -> loadCleaners(txtSearch.getText()));
        table.getSelectionModel().addListSelectionListener(this::tableSelectionChanged);
        pack();
    }

    private void addField(JPanel panel, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0; panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1; panel.add(field, c);
    }

    private void addCleaner() {
        Cleaner cleaner = readAndValidate(); if (cleaner == null) return;
        try {
            if (cleanerDAO.employeeNumberExists(cleaner.getEmployeeNumber(), null)) { showWarning("This employee number already exists."); return; }
            if (cleanerDAO.emailExists(cleaner.getEmail(), null)) { showWarning("A cleaner with this email address already exists."); return; }
            if (cleanerDAO.add(cleaner)) { JOptionPane.showMessageDialog(this, "Cleaner added successfully."); clearForm(); loadCleaners(""); }
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private void updateCleaner() {
        if (selectedId == null) { showWarning("Select a cleaner to update."); return; }
        Cleaner cleaner = readAndValidate(); if (cleaner == null) return; cleaner.setId(selectedId);
        try {
            if (cleanerDAO.employeeNumberExists(cleaner.getEmployeeNumber(), selectedId)) { showWarning("Another cleaner uses this employee number."); return; }
            if (cleanerDAO.emailExists(cleaner.getEmail(), selectedId)) { showWarning("Another cleaner uses this email address."); return; }
            if (cleanerDAO.update(cleaner)) { JOptionPane.showMessageDialog(this, "Cleaner updated successfully."); clearForm(); loadCleaners(txtSearch.getText()); }
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private void deleteCleaner() {
        if (selectedId == null) { showWarning("Select a cleaner to delete."); return; }
        int choice = JOptionPane.showConfirmDialog(this, "Delete the selected cleaner?", "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (cleanerDAO.delete(selectedId)) { JOptionPane.showMessageDialog(this, "Cleaner deleted successfully."); clearForm(); loadCleaners(txtSearch.getText()); }
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private Cleaner readAndValidate() {
        String name = txtFullName.getText().trim(); String employeeNo = txtEmployeeNumber.getText().trim();
        String phone = txtPhone.getText().trim(); String email = txtEmail.getText().trim();
        String department = String.valueOf(cmbDepartment.getEditor().getItem()).trim();
        if (name.isEmpty() || employeeNo.isEmpty() || phone.isEmpty() || email.isEmpty() || department.isEmpty()) { showWarning("Complete all required fields."); return null; }
        if (name.length() < 3) { showWarning("Full name must contain at least 3 characters."); return null; }
        if (!employeeNo.matches("[A-Za-z0-9-]{2,40}")) { showWarning("Employee number may only contain letters, numbers and hyphens."); return null; }
        if (!PHONE.matcher(phone).matches()) { showWarning("Enter a valid phone number."); return null; }
        if (!EMAIL.matcher(email).matches()) { showWarning("Enter a valid email address."); return null; }
        return new Cleaner(name, employeeNo, phone, email, department);
    }

    private void loadCleaners(String term) {
        try {
            List<Cleaner> cleaners = cleanerDAO.search(term); tableModel.setRowCount(0);
            for (Cleaner c : cleaners) tableModel.addRow(new Object[]{c.getId(), c.getFullName(), c.getEmployeeNumber(), c.getPhone(), c.getEmail(), c.getDepartment()});
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private void tableSelectionChanged(ListSelectionEvent event) {
        if (event.getValueIsAdjusting() || table.getSelectedRow() < 0) return;
        int row = table.convertRowIndexToModel(table.getSelectedRow()); selectedId = (Integer) tableModel.getValueAt(row, 0);
        txtFullName.setText(String.valueOf(tableModel.getValueAt(row, 1))); txtEmployeeNumber.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtPhone.setText(String.valueOf(tableModel.getValueAt(row, 3))); txtEmail.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        cmbDepartment.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void clearForm() {
        selectedId = null; txtFullName.setText(""); txtEmployeeNumber.setText(""); txtPhone.setText(""); txtEmail.setText("");
        cmbDepartment.setSelectedIndex(0); table.clearSelection(); txtFullName.requestFocusInWindow();
    }

    private void showWarning(String message) { JOptionPane.showMessageDialog(this, message, "Validation", JOptionPane.WARNING_MESSAGE); }
    private void showDatabaseError(SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE); }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new CleanerForm().setVisible(true)); }
}
