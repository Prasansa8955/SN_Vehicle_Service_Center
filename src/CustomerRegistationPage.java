import Data.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerRegistationPage extends JFrame {

    private static int vehicleIdCounter = 1;

    // UI Components
    private JTextField txtCustomerName, txtNIC, txtAddress, txtContactNumber, txtVehicleNumber, txtVehicleModel;
    private JComboBox<String> cmbVehicleType, cmbServiceType, cmbPriority;
    private JLabel lblVehicleID, lblDate;
    private JTable table;
    private DefaultTableModel tableModel;

    public CustomerRegistationPage() {
        setTitle("SL Vehicle Service Center - Customer Registration");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        // Background image
        JLabel bgLabel = new JLabel(new ImageIcon("src/images/oo.jpg"));
        bgLabel.setBounds(0, 0, 900, 600);
        bgLabel.setLayout(null);
        add(bgLabel);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBounds(80, 60, 740, 470);
        contentPanel.setBackground(new Color(0, 0, 0, 120));
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        bgLabel.add(contentPanel);

        // Titles
        JLabel lblTitle = new JLabel("Customer & Vehicle Registration", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.BLUE);
        lblTitle.setBounds(180, 10, 380, 40);
        bgLabel.add(lblTitle);

         
        
        txtCustomerName = addLabelAndTextField(contentPanel, "Name:", 60);
        txtNIC = addLabelAndTextField(contentPanel, "NIC:", 95);
        txtAddress = addLabelAndTextField(contentPanel, "Address:", 130);
        txtContactNumber = addLabelAndTextField(contentPanel, "Contact No:", 165);

        JLabel lblVType = new JLabel("Vehicle Type:");
        lblVType.setForeground(Color.WHITE);
        lblVType.setBounds(40, 200, 100, 25);
        contentPanel.add(lblVType);
        cmbVehicleType = new JComboBox<>(new String[]{"Car", "Bike", "Van", "Bus", "Lorry"});
        cmbVehicleType.setBounds(150, 200, 180, 25);
        contentPanel.add(cmbVehicleType);

        JLabel lblDateLabel = new JLabel("Date:");
        lblDateLabel.setForeground(Color.WHITE);
        lblDateLabel.setBounds(40, 235, 100, 25);
        contentPanel.add(lblDateLabel);
        lblDate = new JLabel(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        lblDate.setForeground(Color.WHITE);
        lblDate.setBounds(150, 235, 180, 25);
        contentPanel.add(lblDate);

        JLabel lblVehTitle = new JLabel("Vehicle Details");
        lblVehTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblVehTitle.setForeground(Color.WHITE);
        lblVehTitle.setBounds(400, 20, 200, 30);
        contentPanel.add(lblVehTitle);

        JLabel lblVID = new JLabel("Vehicle ID:");
        lblVID.setForeground(Color.WHITE);
        lblVID.setBounds(400, 60, 100, 25);
        contentPanel.add(lblVID);

        lblVehicleID = new JLabel(generateVehicleID());
        lblVehicleID.setForeground(Color.YELLOW);
        lblVehicleID.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblVehicleID.setBounds(510, 60, 180, 25);
        contentPanel.add(lblVehicleID);

        txtVehicleNumber = addLabelAndTextField(contentPanel, "Vehicle No:", 95, 510);
        txtVehicleModel = addLabelAndTextField(contentPanel, "Model:", 130, 510);

        JLabel lblService = new JLabel("Service Type:");
        lblService.setForeground(Color.WHITE);
        lblService.setBounds(400, 165, 100, 25);
        contentPanel.add(lblService);
        cmbServiceType = new JComboBox<>(new String[]{"Full", "Oil", "Half", "Wash", "Brake"});
        cmbServiceType.setBounds(510, 165, 180, 25);
        contentPanel.add(cmbServiceType);

        JLabel lblPriority = new JLabel("Priority:");
        lblPriority.setForeground(Color.WHITE);
        lblPriority.setBounds(400, 200, 100, 25);
        contentPanel.add(lblPriority);
        cmbPriority = new JComboBox<>(new String[]{"Normal", "Urgent", "VIP"});
        cmbPriority.setBounds(510, 200, 180, 25);
        contentPanel.add(cmbPriority);

        // Buttons
        JButton btnAdd = createButton("Add", new Color(0, 153, 76));
        btnAdd.setBounds(400, 240, 80, 30);
        contentPanel.add(btnAdd);

        JButton btnEdit = createButton("Edit", new Color(255, 140, 0));
        btnEdit.setBounds(490, 240, 80, 30);
        contentPanel.add(btnEdit);

        JButton btnDelete = createButton("Delete", new Color(220, 20, 60));
        btnDelete.setBounds(580, 240, 90, 30);
        contentPanel.add(btnDelete);

        JButton btnClear = createButton("Clear", new Color(70, 130, 180));
        btnClear.setBounds(675, 240, 80, 30);
        contentPanel.add(btnClear);

        JButton btnDashboard = createButton("Back", new Color(70, 130, 180));
        btnDashboard.setBounds(520, 420, 120, 30);
        contentPanel.add(btnDashboard);

        // Table
        tableModel = new DefaultTableModel(new Object[]{
                "Name", "NIC", "Address", "Contact", "Date", "Type", "Vehicle ID", "Vehicle No", "Model", "Service", "Priority"
        }, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(40, 280, 680, 150);
        contentPanel.add(scrollPane);

        // Actions
        btnAdd.addActionListener(e -> addCustomer());
        btnEdit.addActionListener(e -> editCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> tableModel.setRowCount(0)); // clear table only
        btnDashboard.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });
    }

    private JTextField addLabelAndTextField(JPanel panel, String text, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(40, y, 100, 25);
        panel.add(lbl);
        JTextField txt = new JTextField();
        txt.setBounds(150, y, 180, 25);
        panel.add(txt);
        return txt;
    }

    private JTextField addLabelAndTextField(JPanel panel, String text, int y, int x) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(400, y, 100, 25);
        panel.add(lbl);
        JTextField txt = new JTextField();
        txt.setBounds(x, y, 180, 25);
        panel.add(txt);
        return txt;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private void addCustomer() {
        String name = txtCustomerName.getText().trim();
        String nic = txtNIC.getText().trim();
        String address = txtAddress.getText().trim();
        String contact = txtContactNumber.getText().trim();
        String vID = lblVehicleID.getText();
        String vNo = txtVehicleNumber.getText().trim();
        String model = txtVehicleModel.getText().trim();
        String vType = (String) cmbVehicleType.getSelectedItem();
        String service = (String) cmbServiceType.getSelectedItem();
        String priority = (String) cmbPriority.getSelectedItem();
        String date = lblDate.getText();

        if (name.isEmpty() || nic.isEmpty() || address.isEmpty() || contact.isEmpty() || vNo.isEmpty() || model.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO customer_vehicle (vehicle_id, customer_name, nic, address, contact_number, date, vehicle_type, vehicle_number, vehicle_model, service_type, priority, service_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, vID);
            pst.setString(2, name);
            pst.setString(3, nic);
            pst.setString(4, address);
            pst.setString(5, contact);
            pst.setString(6, date);
            pst.setString(7, vType);
            pst.setString(8, vNo);
            pst.setString(9, model);
            pst.setString(10, service);
            pst.setString(11, priority);
            pst.setString(12, "Pending");
            pst.executeUpdate();

            tableModel.addRow(new Object[]{name, nic, address, contact, date, vType, vID, vNo, model, service, priority});
            lblVehicleID.setText(generateVehicleID());
            clearFields();
            JOptionPane.showMessageDialog(this, "Record added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    private void editCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to edit!");
            return;
        }

        String vID = (String) tableModel.getValueAt(row, 6);

        String name = txtCustomerName.getText().trim();
        String nic = txtNIC.getText().trim();
        String address = txtAddress.getText().trim();
        String contact = txtContactNumber.getText().trim();
        String vNo = txtVehicleNumber.getText().trim();
        String model = txtVehicleModel.getText().trim();
        String vType = (String) cmbVehicleType.getSelectedItem();
        String service = (String) cmbServiceType.getSelectedItem();
        String priority = (String) cmbPriority.getSelectedItem();
        String date = lblDate.getText();

        if (name.isEmpty() || nic.isEmpty() || address.isEmpty() || contact.isEmpty() || vNo.isEmpty() || model.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields to update!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE customer_vehicle SET customer_name=?, nic=?, address=?, contact_number=?, vehicle_type=?, vehicle_number=?, vehicle_model=?, service_type=?, priority=? WHERE vehicle_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, nic);
            pst.setString(3, address);
            pst.setString(4, contact);
            pst.setString(5, vType);
            pst.setString(6, vNo);
            pst.setString(7, model);
            pst.setString(8, service);
            pst.setString(9, priority);
            pst.setString(10, vID);
            pst.executeUpdate();

            // Update table
            tableModel.setValueAt(name, row, 0);
            tableModel.setValueAt(nic, row, 1);
            tableModel.setValueAt(address, row, 2);
            tableModel.setValueAt(contact, row, 3);
            tableModel.setValueAt(vType, row, 5);
            tableModel.setValueAt(vNo, row, 7);
            tableModel.setValueAt(model, row, 8);
            tableModel.setValueAt(service, row, 9);
            tableModel.setValueAt(priority, row, 10);

            clearFields();
            JOptionPane.showMessageDialog(this, "Record updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to delete!");
            return;
        }

        String vID = (String) tableModel.getValueAt(row, 6);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM customer_vehicle WHERE vehicle_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, vID);
            pst.executeUpdate();

            tableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Record deleted successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        txtCustomerName.setText("");
        txtNIC.setText("");
        txtAddress.setText("");
        txtContactNumber.setText("");
        txtVehicleNumber.setText("");
        txtVehicleModel.setText("");
        cmbVehicleType.setSelectedIndex(0);
        cmbServiceType.setSelectedIndex(0);
        cmbPriority.setSelectedIndex(0);
    }

    private String generateVehicleID() {
        return String.format("VHC-%04d", vehicleIdCounter++);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerRegistationPage().setVisible(true));
    }
}
