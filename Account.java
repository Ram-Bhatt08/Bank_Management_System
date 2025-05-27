public class Account {
    private int accountId;
    private String accountNumber;
    private double balance;

    public Account(int accountId, String accountNumber, double balance) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    // Getters
    public int getAccountId() { return accountId; }
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
}
