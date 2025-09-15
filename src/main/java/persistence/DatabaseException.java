// Package
package persistence;

public class DatabaseException extends Exception {

    // Attributes

    // _____________________________________________

    public DatabaseException(String message) {
        super(message);
    }

    // _____________________________________________

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

} // DatabaseException End