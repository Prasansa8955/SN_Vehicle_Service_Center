import Data.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Stack;
import java.util.UUID;

public class InventoryPage extends JFrame {

    private JTextField txtItemName, txtQuantity, txtPrice;
    private JTable table;
    private DefaultTableModel tableModel;
    private String editingItemId = null;

    // Stack for Undo functionality
    private Stack<Runnable> undoStack = new Stack<>();

    public InventoryPage() {
        setTitle("Vehicle Service Center - Inventory");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        // Background
        JLabel background = new JLabel(new ImageIcon("src/images/bb.jpg"));
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

        JLabel lblTitle = new JLabel("Inventory Management", SwingConstants.CENTER);
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
                "Inventory Details", TitledBorder.LEADING, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(0, 51, 102)));
        background.add(mainPanel);

        // Input fields
        JLabel lblName = new JLabel("Item Name:");
        lblName.setForeground(Color.WHITE); lblName.setBounds(20, 30, 80, 25);
        mainPanel.add(lblName);
        txtItemName = new JTextField(); txtItemName.setBounds(110, 30, 150, 25); mainPanel.add(txtItemName);

        JLabel lblQty = new JLabel("Quantity:");
        lblQty.setForeground(Color.WHITE); lblQty.setBounds(280, 30, 60, 25);
        mainPanel.add(lblQty);
        txtQuantity = new JTextField(); txtQuantity.setBounds(340, 30, 80, 25); mainPanel.add(txtQuantity);

        JLabel lblPrice = new JLabel("Price:");
        lblPrice.setForeground(Color.WHITE); lblPrice.setBounds(440, 30, 50, 25);
        mainPanel.add(lblPrice);
        txtPrice = new JTextField(); txtPrice.setBounds(490, 30, 80, 25); mainPanel.add(txtPrice);

        // Buttons
        JButton btnAdd = createButton("Add Item", new Color(0, 153, 76)); btnAdd.setBounds(600, 25, 100, 35); mainPanel.add(btnAdd);
        JButton btnEdit = createButton("Edit", new Color(255, 165, 0)); btnEdit.setBounds(710, 25, 80, 35); mainPanel.add(btnEdit);
        JButton btnDelete = createButton("Delete", new Color(220, 20, 60)); btnDelete.setBounds(710, 70, 80, 35); mainPanel.add(btnDelete);
        JButton btnClear = createButton("Clear", new Color(128, 0, 128)); btnClear.setBounds(600, 70, 100, 35); mainPanel.add(btnClear);
        JButton btnUndo = createButton("Undo", new Color(255, 140, 0)); btnUndo.setBounds(710, 110, 80, 35); mainPanel.add(btnUndo);
        JButton btnBack = createButton("Back to Dashboard", new Color(70, 130, 180)); btnBack.setBounds(620, 510, 230, 40); background.add(btnBack);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Item ID", "Item Name", "Quantity", "Price"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(0, 102, 204));
        table.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(table); scrollPane.setBounds(20, 150, 780, 220); mainPanel.add(scrollPane);

        // Button actions
        btnAdd.addActionListener(e -> addItem());
        btnEdit.addActionListener(e -> editItem());
        btnDelete.addActionListener(e -> deleteItem());
        btnClear.addActionListener(e -> clearInputs());
        btnBack.addActionListener(e -> { new Dashboard().setVisible(true); dispose(); });
        btnUndo.addActionListener(e -> {
            if(!undoStack.isEmpty()) undoStack.pop().run();
            else JOptionPane.showMessageDialog(this,"Nothing to undo");
        });

        // Table click
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if(row >= 0){
                    editingItemId = table.getValueAt(row, 0).toString();
                    txtItemName.setText(table.getValueAt(row, 1).toString());
                    txtQuantity.setText(table.getValueAt(row, 2).toString());
                    txtPrice.setText(table.getValueAt(row, 3).toString());
                }
            }
        });

        loadInventoryFromDB();
    }

    private void loadInventoryFromDB() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM inventory";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                tableModel.addRow(new Object[]{
                        rs.getString("item_id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage());
        }
    }

    private void addItem() {
        String name = txtItemName.getText().trim();
        String qtyStr = txtQuantity.getText().trim();
        String priceStr = txtPrice.getText().trim();
        if(name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()){ JOptionPane.showMessageDialog(this,"Fill all fields"); return; }

        try(Connection conn = DBConnection.getConnection()){
            int qty = Integer.parseInt(qtyStr);
            double price = Double.parseDouble(priceStr);
            String id = "ITM-" + UUID.randomUUID().toString().substring(0,5);

            String sql = "INSERT INTO inventory(item_id, item_name, quantity, price) VALUES(?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, id); pst.setString(2, name); pst.setInt(3, qty); pst.setDouble(4, price); pst.executeUpdate();

            // Push undo action
            undoStack.push(() -> {
                try(Connection c = DBConnection.getConnection()){
                    String del = "DELETE FROM inventory WHERE item_id=?";
                    PreparedStatement pst2 = c.prepareStatement(del);
                    pst2.setString(1, id);
                    pst2.executeUpdate();
                    loadInventoryFromDB();
                } catch(Exception ex){}
            });

            loadInventoryFromDB();
            clearInputs();
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
    }

    private void editItem() {
        if(editingItemId == null){ JOptionPane.showMessageDialog(this,"Select an item to edit"); return; }
        int row = table.getSelectedRow();
        String prevName = table.getValueAt(row,1).toString();
        int prevQty = Integer.parseInt(table.getValueAt(row,2).toString());
        double prevPrice = Double.parseDouble(table.getValueAt(row,3).toString());

        try(Connection conn = DBConnection.getConnection()){
            int qty = Integer.parseInt(txtQuantity.getText().trim());
            double price = Double.parseDouble(txtPrice.getText().trim());
            String name = txtItemName.getText().trim();

            String sql = "UPDATE inventory SET item_name=?, quantity=?, price=? WHERE item_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name); pst.setInt(2, qty); pst.setDouble(3, price); pst.setString(4, editingItemId);
            pst.executeUpdate();

            // Push undo action
            undoStack.push(() -> {
                try(Connection c = DBConnection.getConnection()){
                    String undoSQL = "UPDATE inventory SET item_name=?, quantity=?, price=? WHERE item_id=?";
                    PreparedStatement pst2 = c.prepareStatement(undoSQL);
                    pst2.setString(1, prevName);
                    pst2.setInt(2, prevQty);
                    pst2.setDouble(3, prevPrice);
                    pst2.setString(4, editingItemId);
                    pst2.executeUpdate();
                    loadInventoryFromDB();
                } catch(Exception ex){}
            });

            loadInventoryFromDB();
            clearInputs();
            editingItemId = null;
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
    }

    private void deleteItem() {
        if(editingItemId == null){ JOptionPane.showMessageDialog(this,"Select an item to delete"); return; }
        int row = table.getSelectedRow();
        String delName = table.getValueAt(row,1).toString();
        int delQty = Integer.parseInt(table.getValueAt(row,2).toString());
        double delPrice = Double.parseDouble(table.getValueAt(row,3).toString());

        try(Connection conn = DBConnection.getConnection()){
            String sql = "DELETE FROM inventory WHERE item_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, editingItemId); pst.executeUpdate();

            // Push undo action
            undoStack.push(() -> {
                try(Connection c = DBConnection.getConnection()){
                    String insertSQL = "INSERT INTO inventory(item_id, item_name, quantity, price) VALUES(?,?,?,?)";
                    PreparedStatement pst2 = c.prepareStatement(insertSQL);
                    pst2.setString(1, editingItemId);
                    pst2.setString(2, delName);
                    pst2.setInt(3, delQty);
                    pst2.setDouble(4, delPrice);
                    pst2.executeUpdate();
                    loadInventoryFromDB();
                } catch(Exception ex){}
            });

            loadInventoryFromDB();
            clearInputs();
            editingItemId = null;
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
    }

    private void clearInputs() {
        txtItemName.setText(""); txtQuantity.setText(""); txtPrice.setText("");
        table.clearSelection(); editingItemId = null;
    }

    private JButton createButton(String text, Color color){
        JButton btn = new JButton(text);
        btn.setBackground(color); btn.setForeground(Color.WHITE); btn.setFont(new Font("Segoe UI",Font.BOLD,13));
        btn.setFocusPainted(false); btn.setBorder(new LineBorder(Color.WHITE,1,true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent evt){ btn.setBackground(color.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt){ btn.setBackground(color); }
        });
        return btn;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new InventoryPage().setVisible(true));
    }
}
