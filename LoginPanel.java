import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.*;

public class LoginPanel extends JPanel {
    public LoginPanel(CardLayout cardLayout, JPanel cardPanel, DashboardPanel dashboardPanel) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Light gray background

        // Header with improved styling
        JPanel headerPanel = UIHelperSwing.createHeader("USER LOGIN", UIHelperSwing.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 15, 0); // Increased spacing

        // Create a white card-like container for the form
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        // Username field with icon
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 40));
        JPanel usernamePanel = UIHelperSwing.createInputPanelWithIcon("Username:", usernameField, 
            UIManager.getIcon("FileView.computerIcon"));
        formCard.add(usernamePanel, gbc);

        // Password field with icon
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 40));
        JPanel passwordPanel = UIHelperSwing.createInputPanelWithIcon("Password:", passwordField, 
            UIManager.getIcon("FileView.floppyDriveIcon"));
        formCard.add(passwordPanel, gbc);

        // Forgot password link
        JLabel forgotPassword = new JLabel("<html><u>Forgot password?</u></html>");
        forgotPassword.setForeground(new Color(100, 100, 255));
        forgotPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Add forgot password functionality
                JOptionPane.showMessageDialog(LoginPanel.this, 
                    "Please contact admin to reset your password", 
                    "Forgot Password", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        gbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(forgotPassword, gbc);

        // Login button with improved styling
        JButton loginButton = UIHelperSwing.createStyledButton("LOGIN", UIHelperSwing.SECONDARY_COLOR);
        loginButton.setPreferredSize(new Dimension(120, 45));
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Add Enter key listener for login
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        });

        loginButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    UIHelperSwing.showErrorDialog(this, "Please enter both username and password");
                    return;
                }

                User user = authenticateUser(username, password);
                if (user != null) {
                    dashboardPanel.setUser(user);
                    cardLayout.show(cardPanel, "dashboard");
                } else {
                    UIHelperSwing.showErrorDialog(this, "Invalid username or password");
                }
            } catch (Exception ex) {
                UIHelperSwing.showErrorDialog(this, "Error during login: " + ex.getMessage());
            }
        });

        // Button panel with improved layout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        // Back button
        JButton backButton = UIHelperSwing.createStyledButton("BACK", new Color(180, 180, 180));
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "welcome"));

        // Create Account button
        JButton createAccountButton = UIHelperSwing.createStyledButton("CREATE ACCOUNT", UIHelperSwing.PRIMARY_COLOR);
        createAccountButton.setPreferredSize(new Dimension(160, 40));
        createAccountButton.addActionListener(e -> cardLayout.show(cardPanel, "register"));

        buttonPanel.add(backButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);

        gbc.insets = new Insets(20, 0, 0, 0);
        formCard.add(buttonPanel, gbc);

        // Add form card to center panel
        centerPanel.add(formCard);
        add(centerPanel, BorderLayout.CENTER);

        // Footer with additional info
        JLabel footerLabel = new JLabel("© 2025 Royal Bank. All rights reserved.", JLabel.CENTER);
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(footerLabel, BorderLayout.SOUTH);
    }

    private User authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone")
                    );
                }
            }
        }
        return null;
    }
}
