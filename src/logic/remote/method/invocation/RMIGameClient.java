/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.remote.method.invocation;

import logic.administration.Administration;
import logic.administration.User;
import logic.fontyspublisher.IRemotePublisherForListener;
import logic.fontyspublisher.RemotePublisher;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The client for RMI
 */
public class RMIGameClient extends Observable implements Serializable
{
    private static final Logger LOGGER = Logger.getLogger(RMIGameClient.class.getName());

    /**
     * The user on who's behalf the client operates
     */
    private User user;

    /**
     * Gets the user object from the game
     * @return a user object
     */
    public User getUser(){return user;}

    /**
     * The binding name for the administration
     */
    private static final String BINDING_NAME = "gameAdmin";

    /**
     * The binding name for the publisher
     */
    private static final String BINDING_NAME_PUBLISHER = "publisher";

    /**
     * References to registry and lobby administration
     */
    private transient Registry registry = null;

    /**
     * The lobby admin which acts as the local lobby admin
     */
    private transient IGameAdmin gameAdmin = null;

    /**
     * The publisher for listener
     */
    private transient IRemotePublisherForListener remotePublisherForListener;

    /**
     * Gets the publisher for listener
     * @return the publisher for listener
     */
    public IRemotePublisherForListener getRpl()
    {
        return remotePublisherForListener;
    }

    /**
     * Constructs the RMI client by use of specified properties
     * @param ipAddress used to construct the RMI client
     */
    public RMIGameClient(String ipAddress, Administration admin)
    {
        callClient(ipAddress, admin);
    }

    /**
     * used to create and initiate the RMI client
     * @param ipAddress of the host
     *
     */
    private void callClient(String ipAddress, Administration admin)
    {
        this.user = admin.getUser();

        // Print IP address and port number for registry
        String ipAddressMessage = String.format("GameClient: ip address: %s", ipAddress);
        LOGGER.log(Level.INFO, ipAddressMessage);
        String portNumberMessage = String.format("GameClient: port number: %s", 1111);
        LOGGER.log(Level.INFO, portNumberMessage);

        // Locate registry at IP address and port number
        try {
            registry = LocateRegistry.getRegistry(ipAddress, 1111);
        } catch (RemoteException ex) {
            LOGGER.log(Level.SEVERE, "GameClient: Cannot locate registry");
            LOGGER.log(Level.SEVERE, "GameClient: remote exception", ex);
            registry = null;
        }

        // Print result locating registry
        if (registry != null) {
            LOGGER.log(Level.INFO, "GameClient: Registry located");
        } else {
            LOGGER.log(Level.WARNING, "GameClient: cannot locate registry");
            LOGGER.log(Level.WARNING, "GameClient: registry is null pointer");
        }

        // Print contents of registry
        if (registry != null) {
            printContentsRegistry();
        }

        // Bind student administration using registry
        if (registry != null) {
            try
            {
                gameAdmin = (IGameAdmin) registry.lookup(BINDING_NAME);
            }
            catch (RemoteException ex)
            {
                LOGGER.log(Level.SEVERE, "GameClient: cannot bind game administration, remote exception", ex);
                gameAdmin = null;
            }
            catch (NotBoundException ex)
            {
                LOGGER.log(Level.SEVERE, "GameClient: cannot bind game administration, not bound exception", ex);
                gameAdmin = null;
            }
        }

        // Bind publisher using registry
        if (registry != null)
        {
            try
            {
                remotePublisherForListener = (IRemotePublisherForListener) registry.lookup(BINDING_NAME_PUBLISHER);
            }
            catch (RemoteException ex)
            {
                LOGGER.log(Level.SEVERE, "GameClient: cannot bind game registry");
                LOGGER.log(Level.SEVERE, "GameClient: remote exception", ex);
                remotePublisherForListener = null;
            }
            catch (NotBoundException ex)
            {
                LOGGER.log(Level.SEVERE, "GameClient: cannot bind game administration");
                LOGGER.log(Level.SEVERE, "GameClient: not bound exception", ex);
                remotePublisherForListener = null;
            }
        }

        // Print result binding student administration
        if (gameAdmin != null)
        {
            LOGGER.log(Level.INFO, "GameClient: game administration bound");
        }
        else
        {
            LOGGER.log(Level.WARNING, "GameClient: game administration is null pointer");
        }

        try
        {
            remotePublisherForListener.subscribeRemoteListener(admin, "playersconnected");
            remotePublisherForListener.subscribeRemoteListener(admin, "gameIsStarted");
        } catch (RemoteException e)
        {
            LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, e);
        }

        // Test RMI connection

        try
        {
            LOGGER.log(Level.INFO, "GameClient: CONNECTED!");
            /*
      The session id of the user
     */
            gameAdmin.connect(user);
        } catch (RemoteException e)
        {
            LOGGER.log(Level.SEVERE, "Remote exception thrown", e);
        }
    }

    /**
     * Connects the client to the game
     * @return the session id of the connected client
     */
    public int getConnected()
    {
        try
        {
            return gameAdmin.getPlayersConnected();
        } catch (RemoteException e)
        {
            LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, e);
            return 0;
        }
    }

    /**
     * Gets the amount of clients currently connected
     * @return an integer value containing the amount of connected clients
     */
    public List<User> getConnectedClients()
    {
        try
        {
            return gameAdmin.getPlayerlist();
        }
        catch (RemoteException e)
        {
            LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, e);
            return new ArrayList<>();
        }
    }


    /**
     * Print contents of registry
     */
    private void printContentsRegistry()
    {
        try
        {
            String[] listOfNames = registry.list();
            LOGGER.log(Level.INFO, "GameClient: list of names bound in registry");

            if (listOfNames.length != 0)
            {
                for (String s : registry.list())
                {
                    LOGGER.log(Level.INFO, s);
                }
            }
            else
            {
                LOGGER.log(Level.INFO, "GameClient: list of names bound in registry is empty");
            }
        }
        catch (RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "GameClient: Cannot show list of names bound in registry");
            LOGGER.log(Level.SEVERE, "GameClient: Remote exception", ex);
        }
    }

    /**
     * Gets the connection properties from the properties file
     * @return a properties object containing the connection values
     */
    public static Properties getConnectionProperties()
    {
        Properties properties = new Properties();

        File file = new File("properties/lobbyAdmin.properties");
        try (InputStream inputStream = new FileInputStream(file))
        {
            properties.load(inputStream);
        }
        catch (IOException e)
        {
            LOGGER.log(Level.CONFIG, "Error reading properties", e);
        }

        return properties;
    }

    /**
     * Informs the clients the game has started
     */
    public void gameIsStarted()
    {
        try
        {
            gameAdmin.gameIsStarted();
        } catch (RemoteException e)
        {
            LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, e);
        }
    }

    /**
     * Gets an object to communicate to the game server
     * @return an IGameAdmin object to communicate with the server
     */
    public IGameAdmin getGameAdmin()
    {
        return gameAdmin;
    }
}
