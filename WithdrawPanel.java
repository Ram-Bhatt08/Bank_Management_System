import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class WithdrawPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final AccountManagerSwing manager;
    
    private JTextField accNoField;
    private JTextField amountField;

    public WithdrawPanel(CardLayout cardLayout, JPanel cardPanel, AccountManagerSwing manager) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.manager = manager;
        
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Header
        add(UIHelperSwing.createHeader("WITHDRAWAL OPERATION", UIHelperSwing.YELLOW), BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize fields
        accNoField = new JTextField(20);
        amountField = new JTextField(20);

        // Account Number Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Account Number:"), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(accNoField, gbc);

        // Amount Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Withdrawal Amount:"), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(amountField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton backButton = UIHelperSwing.createStyledButton("Back to Dashboard", UIHelperSwing.BLUE);
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "dashboard"));

        JButton withdrawButton = UIHelperSwing.createStyledButton("Withdraw", UIHelperSwing.RED);
        withdrawButton.addActionListener(e -> handleWithdrawal());

        buttonPanel.add(backButton);
        buttonPanel.add(withdrawButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        contentPanel.add(buttonPanel, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void handleWithdrawal() {
        String accNo = accNoField.getText().trim();
        String amountText = amountField.getText().trim();

        // Validate account number
        if (accNo.isEmpty()) {
            showError("Please enter an account number", accNoField);
            return;
        }

        // Validate amount
        if (amountText.isEmpty()) {
            showError("Please enter an amount", amountField);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            // Validate amount value
            if (amount <= 0) {
                showError("Amount must be positive", amountField);
                return;
            }

            if (amount > 50000) { // Example daily withdrawal limit
                showError("Maximum withdrawal amount is ₹50,000 per transaction", amountField);
                return;
            }

            // Find account
            AccountSwing account = manager.findAccount(accNo);
            if (account == null) {
                showError("Account not found! Please check the account number", accNoField);
                return;
            }

            // Check sufficient balance
            if (account.getBalance() < amount) {
                showError("Insufficient balance for this withdrawal", amountField);
                return;
            }

            // Perform withdrawal
            try {
                account.withdraw(amount);
                manager.updateAccountBalance(account);
                
                showSuccess(
                    String.format("<html>Withdrew ₹%.2f from account %s<br>New Balance: ₹%.2f</html>",
                        amount, accNo, account.getBalance()),
                    "Withdrawal Successful"
                );
                
                // Clear fields
                accNoField.setText("");
                amountField.setText("");
                
            } catch (SQLException ex) {
                showError("Database error during withdrawal: " + ex.getMessage(), null);
                ex.printStackTrace();
            } catch (Exception ex) {
                showError("Withdrawal failed: " + ex.getMessage(), null);
            }

        } catch (NumberFormatException ex) {
            showError("Invalid amount format\nPlease enter numbers only (e.g., 1000.50)", amountField);
        }
    }

    private void showError(String message, JComponent focusComponent) {
        UIHelperSwing.showErrorDialog(this, message);
        if (focusComponent != null) {
            focusComponent.requestFocusInWindow();
        }
    }

    private void showSuccess(String message, String title) {
        UIHelperSwing.showMessageDialog(this, message, title, UIHelperSwing.GREEN);
    }
}