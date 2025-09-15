// Package
package persistence;

// Import
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    // Attributes
    private final String USER;
    private final String PASSWORD;
    private final String URL;

    // ________________________________________________

    public Database(String user, String password, String url) throws DatabaseException {
        this.USER = user;
        this.PASSWORD = password;
        this.URL = url;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Fejl ved instantiering af PostgreSQL Driver", e);
        }

    }

    // ________________________________________________

    public Connection getConnection() throws DatabaseException {

        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DatabaseException("Fejl under etablering af forbindelse til database", e);
        }

    }

} // Database End