package bootstrapper;

import logic.remote_method_invocation.RMILobbyClient;
import logic.administration.Administration;
import sample.SampleMain;

import java.rmi.RemoteException;
import java.util.Properties;

/**
 * Bootstrap class to initiate the entire client application
 */
class Program
{
    /**
     * Main entry point of client application
     * @param args array of arguments
     */
    public static void main(String[] args)
    {
        try
        {
            Properties properties = RMILobbyClient.getConnectionProperties();
            System.out.println("properties made");

            RMILobbyClient rmiClient = new RMILobbyClient(properties);
            System.out.println("rmi client created");

            Administration administration = new Administration(rmiClient);
            System.out.println("administration created");

            SampleMain.launchView(args, administration);
        }
        catch(RemoteException ex)
        {
            System.out.println("RemoteException: " + ex.getMessage());
        }
    }
}

