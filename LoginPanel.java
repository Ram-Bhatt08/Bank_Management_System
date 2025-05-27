import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPanel extends JPanel {
    public LoginPanel(CardLayout cardLayout, JPanel cardPanel, DashboardPanel dashboardPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        add(UIHelperSwing.createHeader("USER LOGIN", UIHelperSwing.PRIMARY_COLOR), BorderLayout.NORTH);

        // Center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        centerPanel.add(UIHelperSwing.createInputPanel("Username:", usernameField), gbc);
        centerPanel.add(UIHelperSwing.createInputPanel("Password:", passwordField), gbc);

        JButton loginButton = UIHelperSwing.createStyledButton("Login", UIHelperSwing.SECONDARY_COLOR);
        loginButton.addActionListener(e -> {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    UIHelperSwing.showErrorDialog(this, "Please enter both username and password");
                    return;
                }

                // Authenticate user
                User user = authenticateUser(username, password);
                if (user != null) {
                    // Load user accounts and show dashboard
                    dashboardPanel.setUser(user);
                    cardLayout.show(cardPanel, "dashboard");
                } else {
                    UIHelperSwing.showErrorDialog(this, "Invalid username or password");
                }
            } catch (Exception ex) {
                UIHelperSwing.showErrorDialog(this, "Error during login: " + ex.getMessage());
            }
        });

        JButton backButton = UIHelperSwing.createStyledButton("Back", UIHelperSwing.DANGER_COLOR);
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "welcome"));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(backButton);
        buttonPanel.add(loginButton);

        centerPanel.add(buttonPanel, gbc);
        add(centerPanel, BorderLayout.CENTER);
    }

    private User authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password); // In real app, use hashed passwords
            
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
