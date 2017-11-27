package database.Repositories;

import database.*;

import java.io.*;
import java.util.Properties;

public class Repository {
    private IContext context;
    private Properties properties;

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
        Properties properties = new Properties();

        File file = new File("properties/database.properties");
        try (InputStream inputStream = new FileInputStream(file))
        {
            properties.load(inputStream);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return properties;
    }

    public boolean closeConnection(){
        return context.closeConnection();
    }
}
