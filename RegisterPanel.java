import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegisterPanel extends JPanel {
    public RegisterPanel(CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        add(UIHelperSwing.createHeader("CREATE NEW ACCOUNT", UIHelperSwing.PRIMARY_COLOR), BorderLayout.NORTH);

        // Center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JTextField fullNameField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        centerPanel.add(UIHelperSwing.createInputPanel("Full Name:", fullNameField), gbc);
        centerPanel.add(UIHelperSwing.createInputPanel("Username:", usernameField), gbc);
        centerPanel.add(UIHelperSwing.createInputPanel("Password:", passwordField), gbc);
        centerPanel.add(UIHelperSwing.createInputPanel("Confirm Password:", confirmPasswordField), gbc);
        centerPanel.add(UIHelperSwing.createInputPanel("Email:", emailField), gbc);
        centerPanel.add(UIHelperSwing.createInputPanel("Phone Number:", phoneField), gbc);

        JButton registerButton = UIHelperSwing.createStyledButton("Register", UIHelperSwing.SUCCESS_COLOR);
        registerButton.addActionListener(e -> {
            try {
                // Validate inputs
                String fullName = fullNameField.getText().trim();
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();

                if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || 
                    email.isEmpty() || phone.isEmpty()) {
                    UIHelperSwing.showErrorDialog(this, "All fields are required");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    UIHelperSwing.showErrorDialog(this, "Passwords do not match");
                    return;
                }

                if (password.length() < 8) {
                    UIHelperSwing.showErrorDialog(this, "Password must be at least 8 characters");
                    return;
                }

                // Check if username or email already exists
                if (isUsernameTaken(username)) {
                    UIHelperSwing.showErrorDialog(this, "Username already taken");
                    return;
                }

                if (isEmailTaken(email)) {
                    UIHelperSwing.showErrorDialog(this, "Email already registered");
                    return;
                }

                // Create new user
                if (registerUser(fullName, username, password, email, phone)) {
                    UIHelperSwing.showMessageDialog(this, 
                        "Registration successful! Please login to continue.", 
                        "Success");
                    cardLayout.show(cardPanel, "login");
                } else {
                    UIHelperSwing.showErrorDialog(this, "Registration failed. Please try again.");
                }

            } catch (Exception ex) {
                UIHelperSwing.showErrorDialog(this, "Error during registration: " + ex.getMessage());
            }
        });

        JButton backButton = UIHelperSwing.createStyledButton("Back", UIHelperSwing.DANGER_COLOR);
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "welcome"));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);

        centerPanel.add(buttonPanel, gbc);
        add(centerPanel, BorderLayout.CENTER);
    }

    private boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private boolean isEmailTaken(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private boolean registerUser(String fullName, String username, String password, 
                               String email, String phone) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, email, phone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password); // In production, hash the password
            stmt.setString(3, fullName);
            stmt.setString(4, email);
            stmt.setString(5, phone);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            // Create a default account for the new user
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    createDefaultAccount(userId);
                }
            }
            return true;
        }
    }

    private void createDefaultAccount(int userId) throws SQLException {
        String accountNumber = "ACC" + System.currentTimeMillis();
        String sql = "INSERT INTO accounts (user_id, account_number, balance) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, accountNumber);
            stmt.setDouble(3, 500.00); // Default initial balance
            stmt.executeUpdate();
        }
    }
}
