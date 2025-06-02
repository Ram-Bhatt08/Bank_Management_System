import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccountSwing {
    private static int idCounter = 1000;
    private final String accountNumber;
    private final String holderName;
    private double balance;

    public AccountSwing(String holderName, double initialDeposit) {
        this.accountNumber = "ACC" + (++idCounter);
        this.holderName = holderName;
        this.balance = initialDeposit;
    }

    public AccountSwing(String accountNumber, String holderName, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) throws SQLException {
        if (amount > 0) {
            balance += amount;
            updateBalanceInDatabase();
        }
    }

    public void withdraw(double amount) throws Exception {
        if (amount <= 0) throw new Exception("Amount must be positive!");
        if (amount > balance) throw new Exception("Insufficient balance!");
        balance -= amount;
        updateBalanceInDatabase();
    }

    public void transfer(AccountSwing target, double amount) throws Exception {
        this.withdraw(amount);
        target.deposit(amount);
    }

    public String getAccountDetails() {
        return String.format(
            "<html><b>Account Number:</b> %s<br>" +
            "<b>Holder Name:</b> %s<br>" +
            "<b>Balance:</b> ₹%.2f</html>",
            accountNumber, holderName, balance
        );
    }

    private void updateBalanceInDatabase() throws SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE accounts SET balance = ? WHERE account_number = ?")) {
            stmt.setDouble(1, balance);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
        }
    }
}

