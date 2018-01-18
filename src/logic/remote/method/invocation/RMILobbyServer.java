
package logic.remote.method.invocation;

import logic.fontyspublisher.RemotePublisher;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

class RMILobbyServer
{
    private static final Logger LOGGER = Logger.getLogger(RMILobbyServer.class.getName());
    /**
     * The port number of the server
     */
    private static final int PORT_NUMBER = 1100;

    /**
     * The binding name of the lobby administration
     */
    private static final String BINDING_NAME = "LobbyAdmin";

    /**
     * The binding name of the lobby publisher
     */
    private static final String BINDING_NAME_PUBLISHER = "publisher";

    /**
     * The lobby admin the clients interact with
     */
    private LobbyAdmin lobbyAdmin = null;

    /**
     * Constructs the RMI server
     */
    private RMILobbyServer()
    {

        // Print port number for registry
        String portNumberMessage = String.format("LobbyServer: port number %s", PORT_NUMBER);
        LOGGER.log(Level.INFO, portNumberMessage);

        // Create student administration
        /*
      The publisher which informs the clients of changes in the lobby system
     */
        RemotePublisher publisher = null;
        try
        {
            publisher = new RemotePublisher();
            lobbyAdmin = new LobbyAdmin(publisher);
            LOGGER.log(Level.INFO, "LobbyServer: lobby administration created");
        }
        catch (RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "LobbyServer: cannot create lobby administration, remote exception", ex);
            lobbyAdmin = null;
        }

        //Start the timer to clean the list of lobbies
        updateLobbies();

        // Create registry at port number
        /*
      The registry which the clients communicate with
     */
        Registry registry;
        try
        {
            registry = LocateRegistry.createRegistry(PORT_NUMBER);
            String registryPortNumberMessage = String.format("LobbyServer: registry created on port number %s", PORT_NUMBER);
            LOGGER.log(Level.INFO, registryPortNumberMessage);
        }
        catch (RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "LobbyServer: cannot create registry, remote exception", ex);
            registry = null;
        }

        try
        {
            assert registry != null;
            registry.rebind(BINDING_NAME_PUBLISHER, publisher);
        }
        catch(RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "LobbyServer: cannot bind publisher, remote exception", ex);
        }
        catch (NullPointerException e){
            LOGGER.log(Level.SEVERE, "LobbyServer: cannot bind publisher, null pointer exception", e);
        }
        // Bind student administration using registry
        try
        {
            registry.rebind(BINDING_NAME, lobbyAdmin);
        }
        catch (RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "LobbyServer: cannot bind lobby administration, remote exception", ex);
        }
    }

    /**
     * Start the timer to clean the list of lobbies
     */
    private void updateLobbies()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                lobbyAdmin.cleanLobbies();
            }
        }, 0, 1000);
    }

    /**
     * Print IP addresses and network interfaces
     */
    private static void printIPAddresses()
    {
        try
        {
            InetAddress localhost = InetAddress.getLocalHost();
            String ipAddressMessage = String.format("LobbyServer: ip address %s", localhost.getHostAddress());
            LOGGER.log(Level.INFO, ipAddressMessage);
            // Just in case this host has multiple IP addresses....
            InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
            if (allMyIps != null && allMyIps.length > 1)
            {
                LOGGER.log(Level.INFO, "LobbyServer: full list of ip addresses:");

                for (InetAddress allMyIp : allMyIps)
                {
                    String myIpAddressMessage = allMyIp.toString();
                    LOGGER.log(Level.INFO, myIpAddressMessage);
                }
            }
        }
        catch (UnknownHostException ex)
        {
            LOGGER.log(Level.SEVERE, "LobbyServer: cannot get ip address of local host, unknown host exception", ex);
        }
    }

    /**
     * Starts the RMI server
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Welcome message
        LOGGER.log(Level.INFO, "LobbyServer using registry");
        // Print IP addresses and network interfaces
        printIPAddresses();

        // Create server
        new RMILobbyServer();
    }
}
