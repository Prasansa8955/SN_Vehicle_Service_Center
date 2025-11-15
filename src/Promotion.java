import Data.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.UUID;

public class Promotion extends JFrame {

    private JTextField txtPromoName, txtPromoValue, txtServiceType;
    private JComboBox<String> cmbPriority;
    private JTable table;
    private DefaultTableModel tableModel;
    private String editingPromoId = null;

    public Promotion() {
        setTitle("Vehicle Service Center - Promotions & Discounts");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        // Background
        JLabel background = new JLabel(new ImageIcon("src/images/oo.jpg"));
        background.setBounds(0, 0, 900, 600);
        background.setLayout(null);
        add(background);

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
        header.setLayout(null);
        background.add(header);

        JLabel lblTitle = new JLabel("Promotions & Discounts", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(0, 0, 900, 70);
        header.add(lblTitle);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBounds(30, 90, 820, 400);
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(0, 0, 0, 80));
        mainPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 102, 204), 2, true),
                "Promotion Details", TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(0, 51, 102)));
        background.add(mainPanel);

        // Input fields
        JLabel lblName = new JLabel("Promotion Name:");
        lblName.setForeground(Color.WHITE);
        lblName.setBounds(20, 30, 120, 25);
        mainPanel.add(lblName);

        txtPromoName = new JTextField();
        txtPromoName.setBounds(140, 30, 150, 25);
        mainPanel.add(txtPromoName);

        JLabel lblService = new JLabel("Service Type:");
        lblService.setForeground(Color.WHITE);
        lblService.setBounds(310, 30, 90, 25);
        mainPanel.add(lblService);

        txtServiceType = new JTextField();
        txtServiceType.setBounds(400, 30, 100, 25);
        mainPanel.add(txtServiceType);

        JLabel lblPriority = new JLabel("Priority:");
        lblPriority.setForeground(Color.WHITE);
        lblPriority.setBounds(520, 30, 60, 25);
        mainPanel.add(lblPriority);

        cmbPriority = new JComboBox<>(new String[]{"VIP", "URGENT", "NORMAL"});
        cmbPriority.setBounds(580, 30, 100, 25);
        mainPanel.add(cmbPriority);

        JLabel lblValue = new JLabel("Discount %:");
        lblValue.setForeground(Color.WHITE);
        lblValue.setBounds(700, 30, 80, 25);
        mainPanel.add(lblValue);

        txtPromoValue = new JTextField();
        txtPromoValue.setBounds(780, 30, 60, 25);
        mainPanel.add(txtPromoValue);

        // Buttons
        JButton btnAdd = createButton("Add Promotion", new Color(0, 153, 76));
        btnAdd.setBounds(600, 70, 110, 35);
        mainPanel.add(btnAdd);

        JButton btnEdit = createButton("Edit", new Color(255, 165, 0));
        btnEdit.setBounds(720, 70, 80, 35);
        mainPanel.add(btnEdit);

        JButton btnDelete = createButton("Delete", new Color(220, 20, 60));
        btnDelete.setBounds(720, 110, 80, 35);
        mainPanel.add(btnDelete);

        JButton btnClear = createButton("Clear", new Color(128, 0, 128));
        btnClear.setBounds(600, 110, 110, 35);
        mainPanel.add(btnClear);

        JButton btnBack = createButton("Back to Dashboard", new Color(70, 130, 180));
        btnBack.setBounds(620, 510, 230, 40);
        background.add(btnBack);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Promo ID", "Name", "Service Type", "Priority", "Type", "Value"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(0, 102, 204));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 150, 780, 230);
        mainPanel.add(scrollPane);

        // Button actions
        btnAdd.addActionListener(e -> addPromotion());
        btnEdit.addActionListener(e -> editPromotion());
        btnDelete.addActionListener(e -> deletePromotion());
        btnClear.addActionListener(e -> clearInputs());
        btnBack.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });

        // Table click
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    editingPromoId = table.getValueAt(row, 0).toString();
                    txtPromoName.setText(table.getValueAt(row, 1).toString());
                    txtServiceType.setText(table.getValueAt(row, 2).toString());
                    cmbPriority.setSelectedItem(table.getValueAt(row, 3).toString());
                    txtPromoValue.setText(table.getValueAt(row, 5).toString());
                }
            }
        });

        loadPromotionsFromDB();
    }

    private void loadPromotionsFromDB() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM promotion"; // table name fixed
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("promo_id"),
                        rs.getString("promo_name"),
                        rs.getString("service_type"),
                        rs.getString("priority"),
                        rs.getString("promo_type"),
                        rs.getDouble("promo_value")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    private void addPromotion() {
        String name = txtPromoName.getText().trim();
        String serviceType = txtServiceType.getText().trim();
        String priority = cmbPriority.getSelectedItem().toString();
        String valueStr = txtPromoValue.getText().trim();

        if(name.isEmpty() || serviceType.isEmpty() || valueStr.isEmpty()){
            JOptionPane.showMessageDialog(this,"Fill all required fields");
            return;
        }

        try(Connection conn = DBConnection.getConnection()){
            double value = Double.parseDouble(valueStr);
            String id = "PROMO-" + UUID.randomUUID().toString().substring(0,5);

            String sql = "INSERT INTO promotion(promo_id, promo_name, service_type, priority, promo_type, promo_value) VALUES(?,?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, id);
            pst.setString(2, name);
            pst.setString(3, serviceType);
            pst.setString(4, priority);
            pst.setString(5, "Percentage"); // fixed type
            pst.setDouble(6, value);
            pst.executeUpdate();

            loadPromotionsFromDB();
            clearInputs();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
        }
    }

    private void editPromotion() {
        if(editingPromoId == null){
            JOptionPane.showMessageDialog(this,"Select a promotion to edit");
            return;
        }

        try(Connection conn = DBConnection.getConnection()){
            double value = Double.parseDouble(txtPromoValue.getText().trim());

            String sql = "UPDATE promotion SET promo_name=?, service_type=?, priority=?, promo_type=?, promo_value=? WHERE promo_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtPromoName.getText().trim());
            pst.setString(2, txtServiceType.getText().trim());
            pst.setString(3, cmbPriority.getSelectedItem().toString());
            pst.setString(4, "Percentage");
            pst.setDouble(5, value);
            pst.setString(6, editingPromoId);
            pst.executeUpdate();

            loadPromotionsFromDB();
            clearInputs();
            editingPromoId = null;
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
        }
    }

    private void deletePromotion() {
        if(editingPromoId == null){
            JOptionPane.showMessageDialog(this,"Select a promotion to delete");
            return;
        }

        try(Connection conn = DBConnection.getConnection()){
            String sql = "DELETE FROM promotion WHERE promo_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, editingPromoId);
            pst.executeUpdate();

            loadPromotionsFromDB();
            clearInputs();
            editingPromoId = null;
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
        }
    }

    private void clearInputs() {
        txtPromoName.setText("");
        txtServiceType.setText("");
        txtPromoValue.setText("");
        cmbPriority.setSelectedIndex(0);
        table.clearSelection();
        editingPromoId = null;
    }

    private JButton createButton(String text, Color color){
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI",Font.BOLD,13));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(Color.WHITE,1,true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent evt){ btn.setBackground(color.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt){ btn.setBackground(color); }
        });
        return btn;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new Promotion().setVisible(true));
    }
}
