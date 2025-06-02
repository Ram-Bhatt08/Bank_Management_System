import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class TransferPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final AccountManagerSwing manager;
    
    private JTextField senderAccField;
    private JTextField receiverAccField;
    private JTextField amountField;

    public TransferPanel(CardLayout cardLayout, JPanel cardPanel, AccountManagerSwing manager) {
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
        add(UIHelperSwing.createHeader("FUND TRANSFER", UIHelperSwing.YELLOW), BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize fields
        senderAccField = new JTextField(20);
        receiverAccField = new JTextField(20);
        amountField = new JTextField(20);

        // Sender Account Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Sender's Account Number:"), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(senderAccField, gbc);

        // Receiver Account Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Receiver's Account Number:"), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(receiverAccField, gbc);

        // Amount Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(new JLabel("Transfer Amount:"), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(amountField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton backButton = UIHelperSwing.createStyledButton("Back to Dashboard", UIHelperSwing.BLUE);
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "dashboard"));

        JButton transferButton = UIHelperSwing.createStyledButton("Transfer", UIHelperSwing.PURPLE);
        transferButton.addActionListener(e -> handleTransfer());

        buttonPanel.add(backButton);
        buttonPanel.add(transferButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        contentPanel.add(buttonPanel, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void handleTransfer() {
        String senderAccNo = senderAccField.getText().trim();
        String receiverAccNo = receiverAccField.getText().trim();
        String amountText = amountField.getText().trim();

        // Validate sender account
        if (senderAccNo.isEmpty()) {
            showError("Please enter sender's account number", senderAccField);
            return;
        }

        // Validate receiver account
        if (receiverAccNo.isEmpty()) {
            showError("Please enter receiver's account number", receiverAccField);
            return;
        }

        // Validate amount
        if (amountText.isEmpty()) {
            showError("Please enter transfer amount", amountField);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            // Validate amount value
            if (amount <= 0) {
                showError("Amount must be positive", amountField);
                return;
            }

            if (amount > 1000000) { // Example limit
                showError("Maximum transfer amount is ₹1,000,000", amountField);
                return;
            }

            // Check if accounts are different
            if (senderAccNo.equals(receiverAccNo)) {
                showError("Cannot transfer to the same account!", senderAccField);
                return;
            }

            // Find accounts
            AccountSwing senderAccount = manager.findAccount(senderAccNo);
            AccountSwing receiverAccount = manager.findAccount(receiverAccNo);

            if (senderAccount == null || receiverAccount == null) {
                showError("One or both accounts not found!", 
                    senderAccount == null ? senderAccField : receiverAccField);
                return;
            }

            // Check sufficient balance
            if (senderAccount.getBalance() < amount) {
                showError("Insufficient balance in sender's account", amountField);
                return;
            }

            // Perform transfer
            try {
                senderAccount.transfer(receiverAccount, amount);
                manager.updateAccountBalance(senderAccount);
                manager.updateAccountBalance(receiverAccount);
                
                showSuccess(
                    String.format("<html>Transferred ₹%.2f<br>From: %s<br>To: %s<br>New Balance: ₹%.2f</html>",
                        amount, senderAccNo, receiverAccNo, senderAccount.getBalance()),
                    "Transfer Successful"
                );
                
                // Clear fields
                senderAccField.setText("");
                receiverAccField.setText("");
                amountField.setText("");
                
            } catch (SQLException ex) {
                showError("Database error during transfer: " + ex.getMessage(), null);
                ex.printStackTrace();
            } catch (Exception ex) {
                showError("Transfer failed: " + ex.getMessage(), null);
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
