import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connections and initialization for the banking system.
 */
public class DatabaseManager {

    // Database credentials & connection URL
    private static final String URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String USER = "root";
    private static final String PASSWORD = "Scientist24@";

    // SQL statements for creating tables
    private static final String CREATE_USERS_TABLE = """
        CREATE TABLE IF NOT EXISTS users (
            user_id INT AUTO_INCREMENT PRIMARY KEY,
            username VARCHAR(50) UNIQUE NOT NULL,
            password VARCHAR(255) NOT NULL,
            full_name VARCHAR(100),
            email VARCHAR(100),
            phone VARCHAR(20)
        );
        """;

    // Add more table creation SQL here as needed, for example:
    /*
    private static final String CREATE_ACCOUNTS_TABLE = """
        CREATE TABLE IF NOT EXISTS accounts (
            account_id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            balance DECIMAL(15,2) DEFAULT 0,
            account_type VARCHAR(20),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(user_id)
        );
        """;
    */

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver registered successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver not found! Include it in your library path.");
            e.printStackTrace();
        }
    }

    /**
     * Establishes a connection to the database.
     *
     * @return a new Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Initializes the database by creating necessary tables if they do not exist.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(CREATE_USERS_TABLE);
            System.out.println("Users table verified/created.");

            // Uncomment and add more table initialization if needed:
            // stmt.execute(CREATE_ACCOUNTS_TABLE);
            // System.out.println("Accounts table verified/created.");

        } catch (SQLException e) {
            System.err.println("Database initialization failed!");
            e.printStackTrace();
        }
    }
}

