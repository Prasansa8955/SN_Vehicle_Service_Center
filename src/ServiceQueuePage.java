import Data.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceQueuePage extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public ServiceQueuePage() {
        setTitle("Vehicle Service Center - Service Queue");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        // Header
        JPanel header = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 102, 204), getWidth(), getHeight(), new Color(0, 153, 255));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setBounds(0, 0, 900, 70);
        header.setLayout(new BorderLayout());
        JLabel lblTitle = new JLabel("Vehicle Service Queue", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(lblTitle);
        add(header);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(30, 90, 820, 400);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 102, 204), 2, true),
                "Queue Details", TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(0, 51, 102)));
        add(mainPanel);

        // Table
        tableModel = new DefaultTableModel(
                new Object[]{"Vehicle ID", "Customer Name", "Vehicle No", "Vehicle Type", "Service Type", "Priority", "Status"}, 0
        );
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(0, 102, 204));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 30, 780, 300);
        mainPanel.add(scrollPane);

        // Buttons
        JButton btnMarkComplete = createButton("Mark Completed", new Color(0, 153, 76));
        btnMarkComplete.setBounds(20, 350, 150, 35);
        mainPanel.add(btnMarkComplete);

        JButton btnUpdate = createButton("Update Status", new Color(255, 165, 0));
        btnUpdate.setBounds(190, 350, 150, 35);
        mainPanel.add(btnUpdate);

        JButton btnRemove = createButton("Remove", new Color(220, 20, 60));
        btnRemove.setBounds(360, 350, 150, 35);
        mainPanel.add(btnRemove);

        JButton btnBack = createButton("Back to Dashboard", new Color(70, 130, 180));
        btnBack.setBounds(620, 510, 230, 40);
        add(btnBack);

        // Load queue
        loadQueueFromDB();

        // Button actions
        btnMarkComplete.addActionListener(e -> markAsCompleted());
        btnUpdate.addActionListener(e -> updateStatus());
        btnRemove.addActionListener(e -> removeSelectedRow());
        btnBack.addActionListener(e -> {
            new Dashboard().setVisible(true);
            this.dispose();
        });
    }

   private void loadQueueFromDB() {
    tableModel.setRowCount(0);
    List<QueueItem> queue = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT vehicle_id, customer_name, vehicle_number, vehicle_type, service_type, priority, service_status " +
                     "FROM customer_vehicle";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            queue.add(new QueueItem(
                    rs.getString("vehicle_id"),
                    rs.getString("customer_name"),
                    rs.getString("vehicle_number"),
                    rs.getString("vehicle_type"),
                    rs.getString("service_type"),
                    rs.getString("priority"),
                    rs.getString("service_status")
            ));
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
    }

    // Sort by priority: VIP = 1, URGENT = 2, NORMAL = 3
    queue.sort((a, b) -> Integer.compare(a.getPriorityValue(), b.getPriorityValue()));

    for (QueueItem item : queue) {
        tableModel.addRow(new Object[]{
                item.vehicleId,
                item.customerName,
                item.vehicleNumber,
                item.vehicleType,
                item.serviceType,
                item.priority,
                item.status
        });
    }
}

private static class QueueItem {
    String vehicleId, customerName, vehicleNumber, vehicleType, serviceType, priority, status;
    public QueueItem(String vID, String name, String vNo, String vType, String service, String prio, String status) {
        this.vehicleId = vID;
        this.customerName = name;
        this.vehicleNumber = vNo;
        this.vehicleType = vType;
        this.serviceType = service;
        this.priority = prio;
        this.status = status;
    }

    // Priority value for sorting
    public int getPriorityValue() {
        switch (priority.toUpperCase()) {
            case "VIP": return 1;
            case "URGENT": return 2;
            default: return 3; // NORMAL
        }
    }
}

    private void markAsCompleted() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record!");
            return;
        }
        String vID = (String) tableModel.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE customer_vehicle SET service_status='Completed', service_date=NOW() WHERE vehicle_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, vID);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Service marked as completed!");
            loadQueueFromDB(); // reload table
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    private void updateStatus() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a record!"); return; }
        String[] statuses = {"Pending", "In Progress", "Completed"};
        String current = (String) tableModel.getValueAt(row, 6);
        String newStatus = (String) JOptionPane.showInputDialog(this, "Select new status:", "Update Status",
                JOptionPane.PLAIN_MESSAGE, null, statuses, current);
        if (newStatus != null) {
            tableModel.setValueAt(newStatus, row, 6);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE customer_vehicle SET service_status=? WHERE vehicle_id=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, newStatus);
                pst.setString(2, (String) tableModel.getValueAt(row, 0));
                pst.executeUpdate();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        }
    }

    private void removeSelectedRow() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a record!"); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Remove this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String vID = (String) tableModel.getValueAt(row, 0);
                String sql = "DELETE FROM customer_vehicle WHERE vehicle_id=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, vID);
                pst.executeUpdate();
                loadQueueFromDB();
                JOptionPane.showMessageDialog(this, "Record removed!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        }
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        return btn;
    }

    private static class QueueItem {
        String vehicleId, customerName, vehicleNumber, vehicleType, serviceType, priority, status;
        public QueueItem(String vID, String name, String vNo, String vType, String service, String prio, String status) {
            this.vehicleId = vID;
            this.customerName = name;
            this.vehicleNumber = vNo;
            this.vehicleType = vType;
            this.serviceType = service;
            this.priority = prio;
            this.status = status;
        }
        public int getPriorityValue() {
            switch (priority) {
                case "VIP": return 1;
                case "Urgent": return 2;
                default: return 3; // Normal
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServiceQueuePage().setVisible(true));
    }
}

