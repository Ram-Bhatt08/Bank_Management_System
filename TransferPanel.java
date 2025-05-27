import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransferPanel extends JPanel {
    public TransferPanel(CardLayout cardLayout, JPanel cardPanel, AccountManagerSwing manager) {
        setLayout(new GridLayout(5, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        add(UIHelperSwing.createHeader("FUND TRANSFER", UIHelperSwing.YELLOW));

        JTextField senderAccField = new JTextField();
        JTextField receiverAccField = new JTextField();
        JTextField amountField = new JTextField();

        add(UIHelperSwing.createInputPanel("Sender's Account Number:", senderAccField));
        add(UIHelperSwing.createInputPanel("Receiver's Account Number:", receiverAccField));
        add(UIHelperSwing.createInputPanel("Transfer Amount:", amountField));

        JButton backButton = UIHelperSwing.createStyledButton("Back to Main Menu", UIHelperSwing.BLUE);
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "mainMenu"));

        JButton transferButton = UIHelperSwing.createStyledButton("Transfer", UIHelperSwing.PURPLE);
        transferButton.addActionListener(e -> {
            try {
                String senderAccNo = senderAccField.getText().trim();
                String receiverAccNo = receiverAccField.getText().trim();
                double amount = Double.parseDouble(amountField.getText());

                if (amount <= 0) {
                    UIHelperSwing.showErrorDialog(this, "Amount must be positive");
                    return;
                }

                if (senderAccNo.equals(receiverAccNo)) {
                    UIHelperSwing.showErrorDialog(this, "Cannot transfer to the same account!");
                    return;
                }

                AccountSwing sender = manager.findAccount(senderAccNo);
                AccountSwing receiver = manager.findAccount(receiverAccNo);

                if (sender == null || receiver == null) {
                    UIHelperSwing.showErrorDialog(this, "One or both accounts not found!");
                    return;
                }

                try {
                    sender.transfer(receiver, amount);
                    UIHelperSwing.showMessageDialog(this, 
                        String.format("Transferred ₹%.2f from %s to %s", amount, senderAccNo, receiverAccNo), 
                        "Transfer Successful", 
                        UIHelperSwing.GREEN);
                    cardLayout.show(cardPanel, "mainMenu");
                } catch (Exception ex) {
                    UIHelperSwing.showErrorDialog(this, ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                UIHelperSwing.showErrorDialog(this, "Please enter a valid amount");
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(backButton);
        buttonPanel.add(transferButton);
        add(buttonPanel);
    }
}
