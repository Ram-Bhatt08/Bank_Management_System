import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountManagerSwing {
    private final Map<String, AccountSwing> accounts = new ConcurrentHashMap<>();

    public AccountManagerSwing() {
        loadAccountsFromDatabase();
    }

    private void loadAccountsFromDatabase() {
        accounts.clear();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT a.account_number, u.full_name, a.balance " +
                 "FROM accounts a JOIN users u ON a.user_id = u.user_id")) {

            while (rs.next()) {
                String accNo = rs.getString("account_number").trim();
                String name = rs.getString("full_name");
                double balance = rs.getDouble("balance");

                AccountSwing acc = new AccountSwing(accNo, name, balance);
                accounts.put(accNo, acc);
            }
            System.out.println("Loaded " + accounts.size() + " accounts from database");

        } catch (SQLException e) {
            System.err.println("Failed to load accounts from database: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public AccountSwing createAccount(String holderName, double initialDeposit) {
        if (holderName == null || holderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Holder name cannot be empty");
        }
        if (initialDeposit < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative");
        }

        // In a real system, you'd need to get the user_id first
        // This is simplified for demonstration
        String accountNumber = generateAccountNumber();
        AccountSwing acc = new AccountSwing(accountNumber, holderName.trim(), initialDeposit);
        
        try {
            saveAccountToDatabase(acc, holderName.trim());
            accounts.put(acc.getAccountNumber(), acc);
            System.out.println("Created new account: " + acc.getAccountNumber());
            return acc;
        } catch (SQLException e) {
            System.err.println("Account creation failed: " + e.getMessage());
            throw new RuntimeException("Failed to create account", e);
        }
    }

    private void saveAccountToDatabase(AccountSwing acc, String holderName) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            
            // First create user (simplified - in real system you'd have more user data)
            int userId;
            try (PreparedStatement userStmt = conn.prepareStatement(
                "INSERT INTO users (username, password, full_name, email, phone) " +
                "VALUES (?, 'default_password', ?, 'default@email.com', '0000000000')", 
                Statement.RETURN_GENERATED_KEYS)) {
                
                userStmt.setString(1, holderName.toLowerCase().replace(" ", "_"));
                userStmt.setString(2, holderName);
                userStmt.executeUpdate();
                
                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
            
            // Then create account
            try (PreparedStatement accStmt = conn.prepareStatement(
                "INSERT INTO accounts (user_id, account_number, balance) VALUES (?, ?, ?)")) {
                
                accStmt.setInt(1, userId);
                accStmt.setString(2, acc.getAccountNumber());
                accStmt.setDouble(3, acc.getBalance());
                accStmt.executeUpdate();
            }
            
            conn.commit();
        }
    }

    private String generateAccountNumber() {
        return "ACCT" + System.currentTimeMillis();
    }

    public AccountSwing findAccount(String accNo) {
        if (accNo == null || accNo.trim().isEmpty()) {
            return null;
        }
        
        // Try memory cache first
        AccountSwing account = accounts.get(accNo.trim());
        
        if (account == null) {
            // Fallback to database lookup
            account = findAccountInDatabase(accNo.trim());
            if (account != null) {
                accounts.put(account.getAccountNumber(), account);
            }
        }
        
        return account;
    }

    private AccountSwing findAccountInDatabase(String accNo) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT a.account_number, u.full_name, a.balance " +
                 "FROM accounts a JOIN users u ON a.user_id = u.user_id " +
                 "WHERE a.account_number = ?")) {
            
            stmt.setString(1, accNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String accountNumber = rs.getString("account_number").trim();
                    String name = rs.getString("full_name");
                    double balance = rs.getDouble("balance");
                    return new AccountSwing(accountNumber, name, balance);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database lookup failed for account " + accNo + ": " + e.getMessage());
        }
        return null;
    }

    public void updateAccountBalance(AccountSwing account) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE accounts SET balance = ? WHERE account_number = ?")) {
            
            conn.setAutoCommit(false);
            stmt.setDouble(1, account.getBalance());
            stmt.setString(2, account.getAccountNumber());
            stmt.executeUpdate();
            
            // Record transaction
            try (PreparedStatement txnStmt = conn.prepareStatement(
                "INSERT INTO transactions (account_id, type, amount) " +
                "SELECT account_id, 'DEPOSIT', ? FROM accounts WHERE account_number = ?")) {
                
                txnStmt.setDouble(1, account.getBalance());
                txnStmt.setString(2, account.getAccountNumber());
                txnStmt.executeUpdate();
            }
            
            conn.commit();
        }
    }
}
