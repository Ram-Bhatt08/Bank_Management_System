import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class RegisterPanel extends JPanel {

    public RegisterPanel(CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Light gray background

        // Header with improved styling
        JPanel headerPanel = UIHelperSwing.createHeader("CREATE NEW ACCOUNT", UIHelperSwing.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel with scroll
        JScrollPane scrollPane = new JScrollPane(createFormPanel(cardLayout, cardPanel));
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel(CardLayout cardLayout, JPanel cardPanel) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Form title
        JLabel formTitle = new JLabel("Create Your Account");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        formTitle.setForeground(new Color(60, 60, 60));
        formTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        formTitle.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(formTitle, gbc);

        // Input Fields
        JTextField fullNameField = createStyledTextField();
        JTextField usernameField = createStyledTextField();
        JPasswordField passwordField = createStyledPasswordField();
        JPasswordField confirmPasswordField = createStyledPasswordField();
        JTextField emailField = createStyledTextField();
        JTextField phoneField = createStyledTextField();

        // Add input fields with improved labels
        formPanel.add(createEnhancedInputPanel("Full Name", fullNameField, "Enter your full name"), gbc);
        formPanel.add(createEnhancedInputPanel("Username", usernameField, "Choose a unique username (e.g., Someone123)"), gbc);
        formPanel.add(createEnhancedInputPanel("Password", passwordField, "Minimum 8 characters required"), gbc);
        formPanel.add(createEnhancedInputPanel("Confirm Password", confirmPasswordField, "Re-enter your password"), gbc);
        formPanel.add(createEnhancedInputPanel("Email Address", emailField, "e.g., you@example.com"), gbc);
        formPanel.add(createEnhancedInputPanel("Phone Number", phoneField, "10-digit number without spaces"), gbc);

        // Password strength indicator
        JPanel strengthPanel = createPasswordStrengthIndicator(passwordField);
        formPanel.add(strengthPanel, gbc);

        // Terms and conditions checkbox
        JCheckBox termsCheck = new JCheckBox("I agree to the Terms and Conditions");
        termsCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        termsCheck.setBackground(new Color(245, 245, 245));
        termsCheck.setFocusPainted(false);
        formPanel.add(termsCheck, gbc);

        // Buttons with improved styling
        JButton backButton = createModernButton("Back", UIHelperSwing.DANGER_COLOR);
        JButton registerButton = createModernButton("Register", UIHelperSwing.SUCCESS_COLOR);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);
        formPanel.add(buttonPanel, gbc);

        // Sign In Link with improved styling
        JPanel signInPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        signInPanel.setBackground(new Color(245, 245, 245));

        JLabel questionLabel = new JLabel("Already have an account?");
        questionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        questionLabel.setForeground(new Color(100, 100, 100));

        JLabel signInLabel = new JLabel("<html><u><b>Sign In</b></u></html>");
        signInLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signInLabel.setForeground(new Color(0, 105, 204)); // Nice blue color
        signInLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        signInLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, "login");
            }
            public void mouseEntered(MouseEvent e) {
                signInLabel.setForeground(new Color(0, 75, 154)); // Darker on hover
            }
            public void mouseExited(MouseEvent e) {
                signInLabel.setForeground(new Color(0, 105, 204));
            }
        });

        signInPanel.add(questionLabel);
        signInPanel.add(signInLabel);
        formPanel.add(signInPanel, gbc);

        // Button Actions
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "welcome"));

        registerButton.addActionListener(e -> {
            try {
                if (!termsCheck.isSelected()) {
                    UIHelperSwing.showErrorDialog(this, "Please agree to the Terms and Conditions.");
                    return;
                }

                String fullName = fullNameField.getText().trim();
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();

                // Validation with more user-friendly messages
                if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() ||
                        confirmPassword.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    UIHelperSwing.showErrorDialog(this, "All fields are required. Please complete the form.");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    UIHelperSwing.showErrorDialog(this, "The passwords you entered don't match. Please try again.");
                    return;
                }

                if (password.length() < 8) {
                    UIHelperSwing.showErrorDialog(this, "For your security, please choose a password with at least 8 characters.");
                    return;
                }

                if (!isValidEmail(email)) {
                    UIHelperSwing.showErrorDialog(this, "The email address you entered doesn't look right. Please check it.");
                    return;
                }

                if (!isValidPhone(phone)) {
                    UIHelperSwing.showErrorDialog(this, "Please enter a valid 10-digit phone number without spaces or special characters.");
                    return;
                }

                if (isUsernameTaken(username)) {
                    UIHelperSwing.showErrorDialog(this, "This username is already taken. Please choose another one.");
                    return;
                }

                if (isEmailTaken(email)) {
                    UIHelperSwing.showErrorDialog(this, "This email is already registered. Did you mean to sign in?");
                    return;
                }

                // Register with loading indicator
                registerButton.setEnabled(false);
                registerButton.setText("Registering...");
                
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return registerUser(fullName, username, password, email, phone);
                    }

                    @Override
                    protected void done() {
                        try {
                            if (get()) {
                                UIHelperSwing.showMessageDialog(RegisterPanel.this, 
                                    "Registration successful! You can now sign in with your new account.", 
                                    "Success");
                                cardLayout.show(cardPanel, "login");
                            } else {
                                UIHelperSwing.showErrorDialog(RegisterPanel.this, 
                                    "We couldn't complete your registration. Please try again.");
                            }
                        } catch (Exception ex) {
                            UIHelperSwing.showErrorDialog(RegisterPanel.this, 
                                "An error occurred: " + ex.getMessage());
                        } finally {
                            registerButton.setEnabled(true);
                            registerButton.setText("Register");
                        }
                    }
                };
                worker.execute();

            } catch (Exception ex) {
                UIHelperSwing.showErrorDialog(this, "Error: " + ex.getMessage());
            }
        });

        return formPanel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(300, 36));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(300, 36));
        return field;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkenColor(bgColor, 0.1));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private Color darkenColor(Color color, double fraction) {
        int red = (int) Math.round(color.getRed() * (1.0 - fraction));
        int green = (int) Math.round(color.getGreen() * (1.0 - fraction));
        int blue = (int) Math.round(color.getBlue() * (1.0 - fraction));
        
        return new Color(
            Math.max(red, 0),
            Math.max(green, 0),
            Math.max(blue, 0),
            color.getAlpha()
        );
    }

    private JPanel createEnhancedInputPanel(String labelText, JComponent field, String hint) {
        JPanel panel = new JPanel(new BorderLayout(5, 3));
        panel.setBackground(new Color(245, 245, 245));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(80, 80, 80));
        panel.add(label, BorderLayout.NORTH);
        
        panel.add(field, BorderLayout.CENTER);
        
        JLabel hintLabel = new JLabel(hint);
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hintLabel.setForeground(new Color(120, 120, 120));
        panel.add(hintLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createPasswordStrengthIndicator(JPasswordField passwordField) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setVisible(false); // Initially hidden
        
        JLabel strengthLabel = new JLabel("Password strength: ");
        strengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        strengthLabel.setForeground(new Color(120, 120, 120));
        
        JProgressBar strengthBar = new JProgressBar(0, 4);
        strengthBar.setStringPainted(false);
        strengthBar.setForeground(Color.RED);
        strengthBar.setBackground(new Color(220, 220, 220));
        strengthBar.setPreferredSize(new Dimension(100, 5));
        
        JPanel strengthContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        strengthContainer.setBackground(new Color(245, 245, 245));
        strengthContainer.add(strengthLabel);
        strengthContainer.add(strengthBar);
        
        panel.add(strengthContainer, BorderLayout.WEST);
        
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            
            private void update() {
                String password = new String(passwordField.getPassword());
                if (password.isEmpty()) {
                    panel.setVisible(false);
                } else {
                    panel.setVisible(true);
                    int strength = calculatePasswordStrength(password);
                    strengthBar.setValue(strength);
                    
                    // Update color based on strength
                    if (strength < 2) {
                        strengthBar.setForeground(Color.RED);
                    } else if (strength < 4) {
                        strengthBar.setForeground(Color.ORANGE);
                    } else {
                        strengthBar.setForeground(new Color(0, 180, 0)); // Green
                    }
                }
            }
        });
        
        return panel;
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;
        
        // Length check
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;
        
        // Complexity checks
        if (password.matches(".*[A-Z].*")) strength++; // Uppercase
        if (password.matches(".*[0-9].*")) strength++; // Numbers
        if (password.matches(".*[^A-Za-z0-9].*")) strength++; // Special chars
        
        return Math.min(strength, 4); // Cap at 4
    }

    // Email format validator (improved regex)
    private boolean isValidEmail(String email) {
        return Pattern.matches("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$", email);
    }

    // Phone number must be exactly 10 digits
    private boolean isValidPhone(String phone) {
        return Pattern.matches("^\\d{10}$", phone);
    }

    private boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean isEmailTaken(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean registerUser(String fullName, String username, String password,
                                 String email, String phone) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, email, phone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // In production, use password hashing
            stmt.setString(3, fullName);
            stmt.setString(4, email);
            stmt.setString(5, phone);

            int rows = stmt.executeUpdate();
            if (rows == 0) return false;

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int userId = keys.getInt(1);
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
            stmt.setDouble(3, 500.00);
            stmt.executeUpdate();
        }
    }
}
