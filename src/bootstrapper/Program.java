package bootstrapper;

import logic.remote.method.invocation.RMILobbyClient;
import logic.administration.Administration;
import views.LobbyView;

import java.rmi.RemoteException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bootstrap class to initiate the entire client application
 */
class Program
{
    private static final Logger LOGGER = Logger.getLogger(Program.class.getName());

    /**
     * GameView entry point of client application
     * @param args array of arguments
     */
    public static void main(String[] args)
    {
        try
        {
            Properties properties = RMILobbyClient.getConnectionProperties();
            LOGGER.log(Level.INFO, "properties made");

            RMILobbyClient rmiClient = new RMILobbyClient(properties);
            LOGGER.log(Level.INFO,"rmi client created");

            Administration administration = new Administration(rmiClient);
            LOGGER.log(Level.INFO, "administration created");

            LobbyView.launchView(args, administration);
        }
        catch(RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "An error occurred while launching the client application", ex);
        }
    }
}

