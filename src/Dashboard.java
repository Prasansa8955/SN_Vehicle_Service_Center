import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("SL Vehicle Service Center - Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

   
        ImageIcon bgIcon = new ImageIcon("src/images/mm.jpg"); 
        Image bgImage = bgIcon.getImage().getScaledInstance(900, 600, Image.SCALE_SMOOTH);
        JLabel bgLabel = new JLabel(new ImageIcon(bgImage));
        bgLabel.setBounds(0, 0, 900, 600);
        bgLabel.setLayout(null);

    
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(0, 0, 0, 130));
        panel.setBounds(150, 100, 600, 395);
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

    
        JLabel lblWelcome = new JLabel("Welcome to SN Vehicle Service Center");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblWelcome.setForeground(Color.BLUE);
        lblWelcome.setBounds(150, 40, 600, 40);
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        bgLabel.add(lblWelcome);

      
        JButton btnCustomerReg = new JButton("Customer Registration");
        JButton btnServiceQueue = new JButton("Service Queue");
        JButton btnServiceHistory = new JButton("Service History");
        JButton btnInventory = new JButton("Inventory");
        JButton btnBilling = new JButton("Billing");
           JButton btnPromotion = new JButton("Promotions");
        JButton btnLogout = new JButton("Logout");

        JButton[] buttons = {btnCustomerReg, btnServiceQueue, btnServiceHistory, btnInventory, btnBilling,btnPromotion, btnLogout};

        int y = 40;
        for (JButton btn : buttons) {
            btn.setBounds(170, y, 260, 40);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBackground(new Color(0, 153, 204));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(0, 204, 255));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(0, 153, 204));
                }
            });
            y += 50;
            panel.add(btn);
        }

   
        btnLogout.setBackground(new Color(204, 0, 0));
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(255, 51, 51));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(204, 0, 0));
            }
        });

      
        bgLabel.add(panel);
        add(bgLabel);

       
        btnCustomerReg.addActionListener(e -> {
            new CustomerRegistationPage().setVisible(true);
            this.dispose();
        });

        btnServiceQueue.addActionListener(e -> {
            new ServiceQueuePage().setVisible(true);
            this.dispose();
        });

        btnServiceHistory.addActionListener(e -> {
            new ServiceHistoryPage().setVisible(true);
            this.dispose();
        });

        btnInventory.addActionListener(e -> {
            new InventoryPage().setVisible(true);
            this.dispose();
        });

        btnBilling.addActionListener(e -> {
            new BillingPage().setVisible(true);
            this.dispose();
        });
    btnPromotion.addActionListener(e -> {
            new Promotion().setVisible(true);
            this.dispose();
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginPage().setVisible(true);
                this.dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }
}
