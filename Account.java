public class AccountSwing {
    private final String accountNumber;
    private final String holderName;
    private double balance;

    public AccountSwing(String accountNumber, String holderName, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
    }

    public AccountSwing(String holderName, double initialDeposit) {
        this.accountNumber = generateUniqueAccountNumber();
        this.holderName = holderName;
        this.balance = initialDeposit;
    }

    private String generateUniqueAccountNumber() {
        return "ACC" + System.currentTimeMillis();
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

    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) balance -= amount;
    }
}
