import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JPanel {
    private User currentUser;
    private List<Account> userAccounts;
    private JLabel welcomeLabel;
    private JPanel accountsPanel;
    private JPanel actionsPanel;

    public DashboardPanel(CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        welcomeLabel = new JLabel("", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Accounts panel
        accountsPanel = new JPanel();
        accountsPanel.setLayout(new BoxLayout(accountsPanel, BoxLayout.Y_AXIS));
        accountsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        accountsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(accountsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Actions panel
        actionsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        actionsPanel.setBackground(Color.WHITE);

        JButton depositButton = UIHelperSwing.createStyledButton("Deposit", UIHelperSwing.SUCCESS_COLOR);
        depositButton.addActionListener(e -> cardLayout.show(cardPanel, "deposit"));

        JButton withdrawButton = UIHelperSwing.createStyledButton("Withdraw", UIHelperSwing.DANGER_COLOR);
        withdrawButton.addActionListener(e -> cardLayout.show(cardPanel, "withdraw"));

        JButton transferButton = UIHelperSwing.createStyledButton("Transfer", UIHelperSwing.PRIMARY_COLOR);
        transferButton.addActionListener(e -> cardLayout.show(cardPanel, "transfer"));

        JButton logoutButton = UIHelperSwing.createStyledButton("Logout", UIHelperSwing.WARNING_COLOR);
        logoutButton.addActionListener(e -> {
            currentUser = null;
            userAccounts = null;
            cardLayout.show(cardPanel, "welcome");
        });

        actionsPanel.add(depositButton);
        actionsPanel.add(withdrawButton);
        actionsPanel.add(transferButton);
        actionsPanel.add(logoutButton);

        // Add components to main panel
        add(welcomeLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionsPanel, BorderLayout.SOUTH);
    }

    public void setUser(User user) {
        this.currentUser = user;
        refreshUI();
    }

    private void refreshUI() {
        if (currentUser == null) {
            System.err.println("DashboardPanel: currentUser is null!");
            welcomeLabel.setText("Welcome, Guest!");
            accountsPanel.removeAll();
            accountsPanel.add(new JLabel("No user information available."));
            revalidate();
            repaint();
            return;
        }

        welcomeLabel.setText("Welcome, " + currentUser.getFullName() + "!");
        loadUserAccounts();

        accountsPanel.removeAll();

        if (userAccounts == null || userAccounts.isEmpty()) {
            JLabel noAccountsLabel = new JLabel("You don't have any accounts yet.", SwingConstants.CENTER);
            accountsPanel.add(noAccountsLabel);
        } else {
            for (Account account : userAccounts) {
                accountsPanel.add(createAccountCard(account));
                accountsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        revalidate();
        repaint();
    }

    private void loadUserAccounts() {
        userAccounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentUser.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userAccounts.add(new Account(
                        rs.getInt("account_id"),
                        rs.getString("account_number"),
                        rs.getDouble("balance")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createAccountCard(Account account) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel accNumberLabel = new JLabel("Account: " + account.getAccountNumber());
        accNumberLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel balanceLabel = new JLabel(String.format("Balance: ₹%.2f", account.getBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        balanceLabel.setForeground(account.getBalance() >= 0 ? Color.GREEN : Color.RED);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        infoPanel.add(accNumberLabel);
        infoPanel.add(balanceLabel);

        card.add(infoPanel, BorderLayout.CENTER);
        return card;
    }
}

