package database.Contexts;

import database.IContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseContext implements IContext {

    private Connection con;
    public boolean testConnection(Properties props){
        return init(props);
    }
    public boolean init(Properties props) {
        try{
            String connectionURL = props.getProperty("jdbc.url");
            String username = props.getProperty("jdbc.username");
            String password = props.getProperty("jdbc.password");
            con = DriverManager.getConnection(connectionURL, username, password);
            return true;
        }
        catch(SQLException e){
            return false;
        }
    }
    public boolean closeConnection(){
        try{
            this.con.close();
            return true;
        }
        catch(SQLException e){
            return false;
        }
    }
}
