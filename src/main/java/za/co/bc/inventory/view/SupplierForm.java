package za.co.bc.inventory.view;

import za.co.bc.inventory.dao.SupplierDAO;
import za.co.bc.inventory.model.Supplier;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class SupplierForm extends JFrame {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+() -]{7,25}$");

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final JTextField txtName = new JTextField();
    private final JTextField txtContact = new JTextField();
    private final JTextField txtPhone = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextArea txtAddress = new JTextArea(3, 20);
    private final JTextField txtSearch = new JTextField(22);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Supplier", "Contact Person", "Phone", "Email", "Address"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private Integer selectedId;

    public SupplierForm() {
        setTitle("Supplier Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(950, 620));
        setLocationRelativeTo(null);
        buildUi();
        loadSuppliers("");
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Supplier Management");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSearch = new JButton("Search");
        JButton btnShowAll = new JButton("Show All");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll);

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);
        top.add(searchPanel, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Supplier Details"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        addField(form, c, 0, "Supplier name*", txtName);
        addField(form, c, 1, "Contact person*", txtContact);
        addField(form, c, 2, "Phone*", txtPhone);
        addField(form, c, 3, "Email*", txtEmail);
        c.gridx = 0; c.gridy = 4; c.weightx = 0; c.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Address*"), c);
        c.gridx = 1; c.weightx = 1;
        txtAddress.setLineWrap(true); txtAddress.setWrapStyleWord(true);
        form.add(new JScrollPane(txtAddress), c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        buttons.add(btnAdd); buttons.add(btnUpdate); buttons.add(btnDelete); buttons.add(btnClear);
        c.gridx = 0; c.gridy = 5; c.gridwidth = 2; c.weightx = 1;
        form.add(buttons, c);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Suppliers"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, tableScroll);
        split.setResizeWeight(0.34);
        root.add(split, BorderLayout.CENTER);
        setContentPane(root);

        btnAdd.addActionListener(e -> addSupplier());
        btnUpdate.addActionListener(e -> updateSupplier());
        btnDelete.addActionListener(e -> deleteSupplier());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> loadSuppliers(txtSearch.getText()));
        btnShowAll.addActionListener(e -> { txtSearch.setText(""); loadSuppliers(""); });
        txtSearch.addActionListener(e -> loadSuppliers(txtSearch.getText()));
        table.getSelectionModel().addListSelectionListener(this::tableSelectionChanged);
        pack();
    }

    private void addField(JPanel panel, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(field, c);
    }

    private void addSupplier() {
        Supplier supplier = readAndValidate();
        if (supplier == null) return;
        try {
            if (supplierDAO.emailExists(supplier.getEmail(), null)) {
                showWarning("A supplier with this email address already exists."); return;
            }
            if (supplierDAO.add(supplier)) {
                JOptionPane.showMessageDialog(this, "Supplier added successfully.");
                clearForm(); loadSuppliers("");
            }
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private void updateSupplier() {
        if (selectedId == null) { showWarning("Select a supplier to update."); return; }
        Supplier supplier = readAndValidate();
        if (supplier == null) return;
        supplier.setId(selectedId);
        try {
            if (supplierDAO.emailExists(supplier.getEmail(), selectedId)) {
                showWarning("Another supplier already uses this email address."); return;
            }
            if (supplierDAO.update(supplier)) {
                JOptionPane.showMessageDialog(this, "Supplier updated successfully.");
                clearForm(); loadSuppliers(txtSearch.getText());
            }
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private void deleteSupplier() {
        if (selectedId == null) { showWarning("Select a supplier to delete."); return; }
        int choice = JOptionPane.showConfirmDialog(this, "Delete the selected supplier?", "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (supplierDAO.delete(selectedId)) {
                JOptionPane.showMessageDialog(this, "Supplier deleted successfully.");
                clearForm(); loadSuppliers(txtSearch.getText());
            }
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private Supplier readAndValidate() {
        String name = txtName.getText().trim();
        String contact = txtContact.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();
        if (name.isEmpty() || contact.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
            showWarning("Complete all required fields."); return null;
        }
        if (name.length() < 2 || contact.length() < 2) { showWarning("Supplier and contact names must contain at least 2 characters."); return null; }
        if (!PHONE.matcher(phone).matches()) { showWarning("Enter a valid phone number."); return null; }
        if (!EMAIL.matcher(email).matches()) { showWarning("Enter a valid email address."); return null; }
        return new Supplier(name, contact, phone, email, address);
    }

    private void loadSuppliers(String term) {
        try {
            List<Supplier> suppliers = supplierDAO.search(term);
            tableModel.setRowCount(0);
            for (Supplier s : suppliers) tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getContactPerson(), s.getPhone(), s.getEmail(), s.getAddress()});
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private void tableSelectionChanged(ListSelectionEvent event) {
        if (event.getValueIsAdjusting() || table.getSelectedRow() < 0) return;
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        selectedId = (Integer) tableModel.getValueAt(row, 0);
        txtName.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txtContact.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtPhone.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtEmail.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtAddress.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void clearForm() {
        selectedId = null;
        txtName.setText(""); txtContact.setText(""); txtPhone.setText(""); txtEmail.setText(""); txtAddress.setText("");
        table.clearSelection(); txtName.requestFocusInWindow();
    }

    private void showWarning(String message) { JOptionPane.showMessageDialog(this, message, "Validation", JOptionPane.WARNING_MESSAGE); }
    private void showDatabaseError(SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE); }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new SupplierForm().setVisible(true)); }
}
