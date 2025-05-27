import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DepositPanel extends JPanel {
    public DepositPanel(CardLayout cardLayout, JPanel cardPanel, AccountManagerSwing manager) {
        setLayout(new GridLayout(4, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        add(UIHelperSwing.createHeader("DEPOSIT OPERATION", UIHelperSwing.YELLOW));

        JTextField accNoField = new JTextField();
        JTextField amountField = new JTextField();

        add(UIHelperSwing.createInputPanel("Account Number:", accNoField));
        add(UIHelperSwing.createInputPanel("Deposit Amount:", amountField));

        JButton backButton = UIHelperSwing.createStyledButton("Back to Main Menu", UIHelperSwing.BLUE);
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "mainMenu"));

        JButton depositButton = UIHelperSwing.createStyledButton("Deposit", UIHelperSwing.GREEN);
        depositButton.addActionListener(e -> {
            try {
                String accNo = accNoField.getText().trim();
                double amount = Double.parseDouble(amountField.getText());

                if (amount <= 0) {
                    UIHelperSwing.showErrorDialog(this, "Amount must be positive");
                    return;
                }

                AccountSwing acc = manager.findAccount(accNo);
                if (acc == null) {
                    UIHelperSwing.showErrorDialog(this, "Account not found!");
                    return;
                }

                acc.deposit(amount);
                UIHelperSwing.showMessageDialog(this, 
                    String.format("Deposited ₹%.2f to account %s", amount, accNo), 
                    "Deposit Successful", 
                    UIHelperSwing.GREEN);
                cardLayout.show(cardPanel, "mainMenu");
            } catch (NumberFormatException ex) {
                UIHelperSwing.showErrorDialog(this, "Please enter a valid amount");
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(backButton);
        buttonPanel.add(depositButton);
        add(buttonPanel);
    }
}