// Package
package persistence;

// Import
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {

    // Attributes
    private String url;
    private String username;
    private String password;

    // ________________________________________________

    public DatabaseConfig() throws Exception {

        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new Exception("config.properties not found");
            }
            props.load(input);
        }

        url = props.getProperty("db.url");
        username = props.getProperty("db.username");
        password = props.getProperty("db.password");

    }

    // ________________________________________________

    public String getUrl() {
        return url;
    }

    // ________________________________________________

    public String getUsername() {
        return username;
    }

    // ________________________________________________

    public String getPassword() {
        return password;
    }

} // DatabaseConfig End