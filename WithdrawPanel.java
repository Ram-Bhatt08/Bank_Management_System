import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WithdrawPanel extends JPanel {
    public WithdrawPanel(CardLayout cardLayout, JPanel cardPanel, AccountManagerSwing manager) {
        setLayout(new GridLayout(4, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        add(UIHelperSwing.createHeader("WITHDRAWAL OPERATION", UIHelperSwing.YELLOW));

        JTextField accNoField = new JTextField();
        JTextField amountField = new JTextField();

        add(UIHelperSwing.createInputPanel("Account Number:", accNoField));
        add(UIHelperSwing.createInputPanel("Withdrawal Amount:", amountField));

        JButton backButton = UIHelperSwing.createStyledButton("Back to Main Menu", UIHelperSwing.BLUE);
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "mainMenu"));

        JButton withdrawButton = UIHelperSwing.createStyledButton("Withdraw", UIHelperSwing.RED);
        withdrawButton.addActionListener(e -> {
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

                try {
                    acc.withdraw(amount);
                    UIHelperSwing.showMessageDialog(this, 
                        String.format("Withdrew ₹%.2f from account %s", amount, accNo), 
                        "Withdrawal Successful", 
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
        buttonPanel.add(withdrawButton);
        add(buttonPanel);
    }
}
