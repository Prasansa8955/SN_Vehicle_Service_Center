import Data.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ServiceHistoryPage extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalIncome, lblTotalVehicles, lblTopVehicle, lblTopCustomer;
    private JButton btnBack;

    public ServiceHistoryPage() {
        setTitle("SL Vehicle Service Center - Service History");
        setSize(900, 650); // slightly bigger for extra labels
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblTitle = new JLabel("Service History", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBounds(300, 10, 300, 40);
        add(lblTitle);

        tableModel = new DefaultTableModel(new Object[]{
                "Vehicle ID", "Customer Name", "Vehicle No", "Vehicle Model",
                "Vehicle Type", "Service Type", "Date", "Cost"
        }, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 70, 780, 400);
        add(scrollPane);

        lblTotalIncome = new JLabel("Total Income: Rs. 0");
        lblTotalIncome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalIncome.setBounds(50, 480, 250, 30);
        add(lblTotalIncome);

        lblTotalVehicles = new JLabel("Total Vehicles: 0");
        lblTotalVehicles.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalVehicles.setBounds(320, 480, 200, 30);
        add(lblTotalVehicles);

        lblTopVehicle = new JLabel("Most Serviced Vehicle Type: N/A");
        lblTopVehicle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTopVehicle.setBounds(50, 510, 400, 30);
        add(lblTopVehicle);

        lblTopCustomer = new JLabel("Top Customer: N/A");
        lblTopCustomer.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTopCustomer.setBounds(50, 540, 400, 30);
        add(lblTopCustomer);

        btnBack = new JButton("Back");
        btnBack.setBounds(750, 570, 100, 35);
        btnBack.setBackground(new Color(128, 0, 128));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(btnBack);

        btnBack.addActionListener(e -> {
            new Dashboard().setVisible(true);
            this.dispose();
        });

        loadServiceHistory();
    }

    private void loadServiceHistory() {
        tableModel.setRowCount(0);
        double totalIncome = 0;
        int totalVehicles = 0;

        // Maps for algorithms
        Map<String, Integer> vehicleCountMap = new HashMap<>();
        Map<String, Integer> customerCountMap = new HashMap<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT vehicle_id, customer_name, vehicle_number, vehicle_model, " +
                         "vehicle_type, service_type, service_date, cost " +
                         "FROM customer_vehicle " +
                         "WHERE service_status='Completed' AND service_date IS NOT NULL " +
                         "ORDER BY service_date DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String vID = rs.getString("vehicle_id");
                String customer = rs.getString("customer_name");
                String vNo = rs.getString("vehicle_number");
                String model = rs.getString("vehicle_model");
                String vType = rs.getString("vehicle_type");
                String service = rs.getString("service_type");
                String date = rs.getString("service_date");
                double cost = rs.getDouble("cost");

                tableModel.addRow(new Object[]{vID, customer, vNo, model, vType, service, date, String.format("%.2f", cost)});
                
                // Update totals
                totalIncome += cost;
                totalVehicles++;

                // Algorithm 1: Most serviced vehicle type
                vehicleCountMap.put(vType, vehicleCountMap.getOrDefault(vType, 0) + 1);

                // Algorithm 2: Top customer
                customerCountMap.put(customer, customerCountMap.getOrDefault(customer, 0) + 1);
            }

            // Update labels
            lblTotalIncome.setText("Total Income: Rs. " + String.format("%.2f", totalIncome));
            lblTotalVehicles.setText("Total Vehicles: " + totalVehicles);

            // Find most serviced vehicle type
            String topVehicle = vehicleCountMap.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
            lblTopVehicle.setText("Most Serviced Vehicle Type: " + topVehicle);

            // Find top customer
            String topCustomer = customerCountMap.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
            lblTopCustomer.setText("Top Customer: " + topCustomer);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServiceHistoryPage().setVisible(true));
    }
}
