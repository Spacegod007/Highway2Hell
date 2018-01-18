/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.remote_method_invocation;

import logic.administration.Administration;
import logic.administration.User;
import logic.fontyspublisher.IRemotePublisherForListener;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

/**
 * The client for RMI
 */
public class RMIGameClient extends Observable implements Serializable
{
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
    private static final String bindingName = "gameAdmin";

    /**
     * The binding name for the publisher
     */
    private static final String bindingNamePublisher = "publisher";

    /**
     * References to registry and lobby administration
     */
    private Registry registry = null;

    /**
     * The lobby admin which acts as the local lobby admin
     */
    private IGameAdmin gameAdmin = null;

    /**
     * The publisher for listener
     */
    private IRemotePublisherForListener remotePublisherForListener;

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
        System.out.println("GameClient: IP Address: " + ipAddress);
        System.out.println("GameClient: Port number " + 1111);

        // Locate registry at IP address and port number
        try {
            registry = LocateRegistry.getRegistry(ipAddress, 1111);
        } catch (RemoteException ex) {
            System.out.println("Client: Cannot locate registry");
            System.out.println("GameClient: RemoteException: " + ex.getMessage());
            registry = null;
        }

        // Print result locating registry
        if (registry != null) {
            System.out.println("Client: Registry located");
        } else {
            System.out.println("Client: Cannot locate registry");
            System.out.println("Client: Registry is null pointer");
        }

        // Print contents of registry
        if (registry != null) {
            printContentsRegistry();
        }

        // Bind student administration using registry
        if (registry != null) {
            try
            {
                gameAdmin = (IGameAdmin) registry.lookup(bindingName);
            }
            catch (RemoteException ex)
            {
                System.out.println("GameClient: Cannot bind lobby administration");
                System.out.println("Client: RemoteException: " + ex.getMessage());
                gameAdmin = null;
            }
            catch (NotBoundException ex)
            {
                System.out.println("Client: Cannot bind lobby administration");
                System.out.println("Client: NotBoundException: " + ex.getMessage());
                gameAdmin = null;
            }
        }

        // Bind publisher using registry
        if (registry != null)
        {
            try
            {
                remotePublisherForListener = (IRemotePublisherForListener) registry.lookup(bindingNamePublisher);
            }
            catch (RemoteException ex)
            {
                System.out.println("GameClient: Cannot bind lobby administration");
                System.out.println("Client: RemoteException: " + ex.getMessage());
                remotePublisherForListener = null;
            }
            catch (NotBoundException ex)
            {
                System.out.println("Client: Cannot bind lobby administration");
                System.out.println("Client: NotBoundException: " + ex.getMessage());
                remotePublisherForListener = null;
            }
        }

        // Print result binding student administration
        if (gameAdmin != null)
        {
            System.out.println("Client: Student administration bound");
        }
        else
        {
            System.out.println("Client: Student administration is null pointer");
        }

        try
        {
            remotePublisherForListener.subscribeRemoteListener(admin, "playersconnected");
            remotePublisherForListener.subscribeRemoteListener(admin, "gameIsStarted");
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }

        // Test RMI connection

        try
        {
            System.out.println("CONNECTED!");
            /*
      The session id of the user
     */
            long sessionId = gameAdmin.connect(user);
        } catch (RemoteException e)
        {
            e.printStackTrace();
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
            e.printStackTrace();
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
        } catch (RemoteException e)
        {
            e.printStackTrace();
            return null;
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
            System.out.println("GameClient: list of names bound in registry:");

            if (listOfNames.length != 0)
            {
                for (String s : registry.list())
                {
                    System.out.println(s);
                }
            }
            else
            {
                System.out.println("Client: list of names bound in registry is empty");
            }
        }
        catch (RemoteException ex)
        {
            System.out.println("Client: Cannot show list of names bound in registry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
        }
    }

    /**
     * Gets the connection properties from the properties file
     * @return a properties object containing the connection values
     */
    // TODO: 4-12-2017 Create properties for GameClient
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
            e.printStackTrace();
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
            e.printStackTrace();
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
