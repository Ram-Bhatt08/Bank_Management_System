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
    public static int getIdCounter() {
    return idCounter;
}
    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public void withdraw(double amount) throws Exception {
        if (amount > balance) throw new Exception("Insufficient balance!");
        if (amount <= 0) throw new Exception("Amount must be positive!");
        balance -= amount;
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
}

