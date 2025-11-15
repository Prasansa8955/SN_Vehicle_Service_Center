import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnClear;
    private JLabel lblStatus;

    public LoginPage() {
        setTitle("SL Vehicle Service Center - Login");
        setSize(900, 600); // Bone size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background Image
        ImageIcon bgIcon = new ImageIcon("src/images/istockphoto.jpg"); 
        Image bgImage = bgIcon.getImage().getScaledInstance(900, 600, Image.SCALE_SMOOTH);
        JLabel bgLabel = new JLabel(new ImageIcon(bgImage));
        bgLabel.setBounds(0, 0, 900, 600);
        bgLabel.setLayout(null);

        //Transparent 
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(new Color(0, 0, 0, 100)); 
        loginPanel.setBounds(260, 130, 380, 300);
        loginPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));

        // Title 
        JLabel lblTitle = new JLabel("SN Vehicle Service Center");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.BLUE);
        lblTitle.setBounds(250, 50, 450, 50);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        //  Labels 
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblUser.setForeground(Color.WHITE);
        lblUser.setBounds(40, 40, 120, 30);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtUsername.setBounds(160, 40, 180, 30);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(40, 100, 120, 30);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtPassword.setBounds(160, 100, 180, 30);

        //  Buttons 
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(new Color(0, 153, 204));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBounds(60, 170, 120, 35);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClear.setBackground(new Color(204, 0, 0));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        btnClear.setBounds(200, 170, 120, 35);
        btnClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        //  Status Label 
        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setForeground(Color.YELLOW);
        lblStatus.setBounds(50, 220, 280, 30);

        // Add Components to Panel 
        loginPanel.add(lblUser);
        loginPanel.add(txtUsername);
        loginPanel.add(lblPass);
        loginPanel.add(txtPassword);
        loginPanel.add(btnLogin);
        loginPanel.add(btnClear);
        loginPanel.add(lblStatus);

        
        bgLabel.add(lblTitle);
        bgLabel.add(loginPanel);

        //  Add to Frame 
        add(bgLabel);

        // Actions 
        btnLogin.addActionListener(e -> checkLogin());
        btnClear.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
            lblStatus.setText("");
        });
    }

    private void checkLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.equals("admin") && pass.equals("1234")) {
            JOptionPane.showMessageDialog(this, "Welcome Admin!");
            new Dashboard().setVisible(true);
            this.dispose();
        } else {
            lblStatus.setText("Invalid username or password!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
