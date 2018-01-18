package database.repositories;

import database.*;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Repository {
    private static final Logger LOGGER = Logger.getLogger(Repository.class.getName());

    private final IContext context;
    private final Properties properties;

    public Repository(IContext context)
    {
        this.context = context;
        this.properties = getDatabaseProperties();
    }

    public boolean testConnection()
    {
        return context.testConnection(properties);
    }

    private Properties getDatabaseProperties()
    {
        Properties loadedProperties = new Properties();

        File file = new File("loadedProperties/database.loadedProperties");
        try (InputStream inputStream = new FileInputStream(file))
        {
            loadedProperties.load(inputStream);
        } catch (IOException e)
        {
            LOGGER.log(Level.CONFIG, "Exception in loading database properties", e);
        }

        return loadedProperties;
    }

    public boolean closeConnection(){
        return context.closeConnection();
    }
}
