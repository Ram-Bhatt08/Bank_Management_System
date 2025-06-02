import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class BankingAppSwing {
    public static void main(String[] args) {
        // Initialize database
        DatabaseManager.initializeDatabase();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Royal Bank");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            CardLayout cardLayout = new CardLayout();
            JPanel cardPanel = new JPanel(cardLayout);

            // Core logic handler for account operations
            AccountManagerSwing accountManager = new AccountManagerSwing();

            // UI Panels
            WelcomePanel welcomePanel = new WelcomePanel(cardLayout, cardPanel);
            DashboardPanel dashboardPanel = new DashboardPanel(cardLayout, cardPanel);
            LoginPanel loginPanel = new LoginPanel(cardLayout, cardPanel, dashboardPanel);
            RegisterPanel registerPanel = new RegisterPanel(cardLayout, cardPanel);

            // Transaction Panels
            DepositPanel depositPanel = new DepositPanel(cardLayout, cardPanel, accountManager);
            WithdrawPanel withdrawPanel = new WithdrawPanel(cardLayout, cardPanel, accountManager);
            TransferPanel transferPanel = new TransferPanel(cardLayout, cardPanel, accountManager);

            // Add all views to the cardPanel
            cardPanel.add(welcomePanel, "welcome");
            cardPanel.add(dashboardPanel, "dashboard");
            cardPanel.add(loginPanel, "login");
            cardPanel.add(registerPanel, "register");
            cardPanel.add(depositPanel, "deposit");
            cardPanel.add(withdrawPanel, "withdraw");
            cardPanel.add(transferPanel, "transfer");

            frame.add(cardPanel);
            frame.setVisible(true);
        });
    }
}

