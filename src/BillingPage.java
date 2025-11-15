import Data.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillingPage extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton generateBtn, backBtn;

    // Promotion
    private List<Promo> promotions = new ArrayList<>();

    public BillingPage() {
        setTitle("Billing Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);

        
        JLabel background = new JLabel(new ImageIcon("src/images/oo.jpg"));
        background.setLayout(null);
        setContentPane(background);

        // Title
        JLabel title = new JLabel("Billing Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.BLUE);
        title.setBounds(0, 20, 950, 50);
        background.add(title);

        // Table
        String[] columns = {"Customer Name", "Vehicle ID", "Vehicle Number", "Vehicle Type", "Model",
                "Service Type", "Priority", "Base Cost", "Discount", "Final Cost", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(25, 100, 900, 350);
        background.add(scrollPane);

        // Buttons
        generateBtn = new JButton("Generate Bills");
        backBtn = new JButton("Back");
        styleButton(generateBtn, new Color(0, 153, 204));
        styleButton(backBtn, new Color(204, 0, 0));
        generateBtn.setBounds(250, 470, 200, 40);
        backBtn.setBounds(500, 470, 200, 40);
        background.add(generateBtn);
        background.add(backBtn);

        // Button actions
        generateBtn.addActionListener(e -> generateAllBills());
        backBtn.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });

        // Load promotion
        loadPromotions();
    }

    // Button style
    private void styleButton(JButton btn, Color baseColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(baseColor.brighter()); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(baseColor); }
        });
    }

    // Load promotion in mysql
    private void loadPromotions() {
        promotions.clear();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT promo_id, promo_name, service_type, priority, promo_value FROM promotion";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                promotions.add(new Promo(
                        rs.getString("promo_id"),
                        rs.getString("promo_name"),
                        rs.getString("service_type"),
                        rs.getString("priority"),
                        rs.getDouble("promo_value")
                ));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading promotions: " + ex.getMessage());
        }
    }

    // Get discount 
    private double getDiscountPercent(String serviceType, String priority) {
        for (Promo promo : promotions) {
            if (promo.serviceType.equalsIgnoreCase(serviceType) &&
                promo.priority.equalsIgnoreCase(priority)) {
                return promo.value;
            }
        }
        return 0.0;
    }

   
    private void generateAllBills() {
        tableModel.setRowCount(0); 

        try (Connection conn = DBConnection.getConnection()) {
           
            String query = "SELECT vehicle_id, customer_name, vehicle_number, vehicle_type, vehicle_model, " +
                    "service_type, priority, cost, service_status, IFNULL(bill_generated,0) as bill_generated " +
                    "FROM customer_vehicle";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String vID = rs.getString("vehicle_id");
                String name = rs.getString("customer_name");
                String vNo = rs.getString("vehicle_number");
                String vType = rs.getString("vehicle_type");
                String vModel = rs.getString("vehicle_model");
                String serviceType = rs.getString("service_type");
                String priority = rs.getString("priority");
                double storedCost = rs.getDouble("cost");
                String status = rs.getString("service_status");
                int billGeneratedFlag = rs.getInt("bill_generated");

               
                double baseCost = storedCost;
                if (baseCost <= 0) {
                    baseCost = calculateServiceCost(serviceType, vType, vModel, priority);
                }

                // Apply promotion
                double discountPercent = getDiscountPercent(serviceType, priority);
                double discountAmount = baseCost * discountPercent / 100.0;
                double finalCost = baseCost - discountAmount;
                if (finalCost < 0) finalCost = 0;

               
                if (billGeneratedFlag == 0) {
                    try {
                        String updateSQL = "UPDATE customer_vehicle SET cost = ?, service_date = NOW(), bill_generated = 1 WHERE vehicle_id = ?";
                        PreparedStatement updatePst = conn.prepareStatement(updateSQL);
                        updatePst.setDouble(1, finalCost); // store final cost
                        updatePst.setString(2, vID);
                        updatePst.executeUpdate();
                    } catch (SQLException ex) {
                      
                        JOptionPane.showMessageDialog(this, "Error saving bill for " + vID + ": " + ex.getMessage());
                    }
                }

                // Add row to table 
                tableModel.addRow(new Object[]{
                        name, vID, vNo, vType, vModel, serviceType, priority,
                        String.format("%.2f", baseCost),             
                        String.format("%.2f", discountAmount),        
                        String.format("%.2f", finalCost),            
                        status
                });
            }

            JOptionPane.showMessageDialog(this, "Bills calculated and saved");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    // Service cost cal
    private double calculateServiceCost(String serviceType, String vehicleType, String model, String priority) {
        double baseCost;
        switch (serviceType == null ? "" : serviceType.toLowerCase()) {
            case "full": baseCost = 12000; break;
            case "oil": baseCost = 5000; break;
            case "half": baseCost = 7000; break;
            case "wash": baseCost = 2000; break;
            case "brake": baseCost = 8000; break;
            default: baseCost = 4000; break;
        }

        if (vehicleType != null) {
            if (vehicleType.equalsIgnoreCase("Car")) baseCost += 2000;
            else if (vehicleType.equalsIgnoreCase("Van")) baseCost += 4000;
            else if (vehicleType.equalsIgnoreCase("Bike")) baseCost -= 1000;
            else if (vehicleType.equalsIgnoreCase("Bus")) baseCost += 5000;
            else if (vehicleType.equalsIgnoreCase("Lorry")) baseCost += 6000;
        }

        if (model != null && model.toLowerCase().contains("hybrid")) baseCost += 3000;
        if (priority != null) {
            if (priority.equalsIgnoreCase("VIP")) baseCost += 5000;
            else if (priority.equalsIgnoreCase("Urgent")) baseCost += 2000;
        }

        return baseCost;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BillingPage().setVisible(true));
    }

    // Promo class
    private static class Promo {
        String promoId, name, serviceType, priority;
        double value;

        public Promo(String promoId, String name, String serviceType, String priority, double value) {
            this.promoId = promoId;
            this.name = name;
            this.serviceType = serviceType;
            this.priority = priority;
            this.value = value;
        }
    }
}
