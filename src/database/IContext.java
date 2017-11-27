package database;

import java.util.Properties;

public interface IContext{

    /**
     * Tests the connection to the database.
     * @param props the properties to login to the database.
     * @return true on the condition the connection succeeds, false if it fails.
     */
    boolean testConnection(Properties props);

    /**
     * Closes the connection to the database
     * @return true on the condition closing the connection succeeds, false if an unexpected error occurs.
     */
    boolean closeConnection();
}
