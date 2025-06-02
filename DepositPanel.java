import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class DepositPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final AccountManagerSwing manager;

    public DepositPanel(CardLayout cardLayout, JPanel cardPanel, AccountManagerSwing manager) {
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
        add(UIHelperSwing.createHeader("DEPOSIT OPERATION", UIHelperSwing.YELLOW), BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Input fields
        JTextField accNoField = new JTextField(20);
        JTextField amountField = new JTextField(20);

        // Account Number Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Account Number:"), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(accNoField, gbc);

        // Amount Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Deposit Amount:"), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(amountField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton backButton = UIHelperSwing.createStyledButton("Back to Dashboard", UIHelperSwing.BLUE);
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "dashboard"));

        JButton depositButton = UIHelperSwing.createStyledButton("Deposit", UIHelperSwing.GREEN);
        depositButton.addActionListener(e -> handleDeposit(accNoField, amountField));

        buttonPanel.add(backButton);
        buttonPanel.add(depositButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        contentPanel.add(buttonPanel, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void handleDeposit(JTextField accNoField, JTextField amountField) {
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

            if (amount <= 0) {
                showError("Amount must be positive", amountField);
                return;
            }

            if (amount > 1000000) { // Example limit
                showError("Maximum deposit amount is ₹1,000,000", amountField);
                return;
            }

            // Find account
            AccountSwing acc = manager.findAccount(accNo);
            if (acc == null) {
                showError("Account not found!\nPlease check the account number", accNoField);
                return;
            }

            // Perform deposit
            acc.deposit(amount);
            manager.updateAccountBalance(acc);
            
            // Show success message
            showSuccess(
                String.format("<html>Deposited ₹%.2f to account %s<br>New Balance: ₹%.2f</html>", 
                    amount, accNo, acc.getBalance()),
                "Deposit Successful"
            );
                
            // Clear fields
            accNoField.setText("");
            amountField.setText("");

        } catch (NumberFormatException ex) {
            showError("Invalid amount format\nPlease enter numbers only (e.g., 1000.50)", amountField);
        } catch (SQLException ex) {
            showError("Database error occurred: " + ex.getMessage(), null);
            ex.printStackTrace();
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