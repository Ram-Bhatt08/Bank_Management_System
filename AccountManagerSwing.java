import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountManagerSwing {
    private final Map<String, AccountSwing> accounts = new ConcurrentHashMap<>();

    /**
     * Creates a new account with the given holder name and initial deposit.
     * @param holderName the name of the account holder
     * @param initialDeposit the initial deposit amount (must be >= 0)
     * @return the created AccountSwing instance, or null if input is invalid
     */
    public AccountSwing createAccount(String holderName, double initialDeposit) {
        if (holderName == null || holderName.trim().isEmpty()) {
            System.err.println("Account creation failed: Holder name is empty.");
            return null;
        }
        if (initialDeposit < 0) {
            System.err.println("Account creation failed: Initial deposit cannot be negative.");
            return null;
        }

        AccountSwing acc = new AccountSwing(holderName.trim(), initialDeposit);
        accounts.put(acc.getAccountNumber(), acc);
        System.out.println("Account created: " + acc.getAccountNumber());
        return acc;
    }

    /**
     * Finds an account by account number.
     * @param accNo the account number
     * @return the AccountSwing if found, else null
     */
    public AccountSwing findAccount(String accNo) {
        if (accNo == null || accNo.trim().isEmpty()) {
            System.err.println("Invalid account number lookup.");
            return null;
        }
        return accounts.get(accNo.trim());
    }

    /**
     * Returns all the accounts managed by this manager.
     * @return a map of account number to AccountSwing
     */
    public Map<String, AccountSwing> getAccounts() {
        return accounts;
    }
}

