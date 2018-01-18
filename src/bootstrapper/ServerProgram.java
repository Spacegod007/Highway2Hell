package bootstrapper;

import logic.remote.method.invocation.RMILobbyServer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GameView entry point of server application
 */
public class ServerProgram
{
    private static final Logger LOGGER = Logger.getLogger(ServerProgram.class.getName());

    /**
     * Starts the RMI server
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Welcome message
        LOGGER.log(Level.INFO, "LobbyServer using registry");

        // Print IP addresses and network interfaces
        RMILobbyServer.printIPAddresses();

        // Create server
        new RMILobbyServer();
    }
}
