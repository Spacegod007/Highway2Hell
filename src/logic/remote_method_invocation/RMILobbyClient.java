/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.remote_method_invocation;

import logic.administration.Administration;
import logic.fontyspublisher.IRemotePublisherForListener;
import logic.administration.Lobby;
import logic.administration.User;
import logic.game.CharacterColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The client for RMI
 */
public class RMILobbyClient
{
    /**
     * The binding name for the administration
     */
    private static final String bindingName = "LobbyAdmin";

    /**
     * The binding name for the publisher
     */
    private static final String bindingNamePublisher = "publisher";

    /**
     * The current user
     */
    private User user;

    /**
     * Gets the current user
     * @return the current user
     */
    public User getUser()
    {
        return user;
    }

    /**
     * References to registry and lobby administration
     */
    private Registry registry = null;

    /**
     * The lobby admin which acts as the local lobby admin
     */
    private ILobbyAdmin lobbyAdmin = null;

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
     * @param properties used to construct the RMI client
     */
    public RMILobbyClient(Properties properties)
    {
        String ip = properties.getProperty("ipAddress");
        int port = Integer.parseInt(properties.getProperty("port"));

        callClient(ip, port);
    }

    /**
     * Constructs the RMI client
     * @param ipAddress of the host
     * @param portNumber of the host should he decide to open a lobby
     */
    public RMILobbyClient(String ipAddress, int portNumber)
    {
        callClient(ipAddress, portNumber);
    }

    /**
     * used to create and initiate the RMI client
     * @param ipAddress of the host
     * @param portNumber of the server-lobby
     */
    private void callClient(String ipAddress, int portNumber)
    {
        // Print IP address and port number for registry
        System.out.println("Client: IP Address: " + ipAddress);
        System.out.println("Client: Port number " + portNumber);

        // Locate registry at IP address and port number
        try {
            registry = LocateRegistry.getRegistry(ipAddress, portNumber);
        } catch (RemoteException ex) {
            System.out.println("Client: Cannot locate registry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
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
                lobbyAdmin = (ILobbyAdmin) registry.lookup(bindingName);
            }
            catch (RemoteException ex)
            {
                System.out.println("Client: Cannot bind lobby administration");
                System.out.println("Client: RemoteException: " + ex.getMessage());
                lobbyAdmin = null;
            }
            catch (NotBoundException ex)
            {
                System.out.println("Client: Cannot bind lobby administration");
                System.out.println("Client: NotBoundException: " + ex.getMessage());
                lobbyAdmin = null;
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
                System.out.println("Client: Cannot bind lobby administration");
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
        if (lobbyAdmin != null)
        {
            System.out.println("Client: Student administration bound");
        }
        else
        {
            System.out.println("Client: Student administration is null pointer");
        }

        // Test RMI connection
        if (lobbyAdmin != null) {
            testConnection();
            user = null;
        }
    }

    /**
     * Sets the username
     * @param username to be set
     * @return a user object
     */
    public User setUsername(String username)
    {
        try
        {
            user = lobbyAdmin.addUser(username);
            return user;
        }
        catch(RemoteException ex)
        {
            System.out.println("Client: User not set");
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
            System.out.println("Client: list of names bound in registry:");

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
     * Add a lobby to the system
     * @param name of the lobby
     * @return The newly created lobby
     */
    public Lobby addLobby(String name, Administration admin)
    {
        Lobby ret = null;
        try
        {
            ret = lobbyAdmin.addLobby(name, user, Inet4Address.getLocalHost().getHostAddress());
            if(ret != null)
            {
                System.out.print("Subscribing on ID: ");
                System.out.println(ret.getId());
                remotePublisherForListener.subscribeRemoteListener(admin, Integer.toString(ret.getId()));
            }
        }
        catch(RemoteException ex)
        {
            System.out.println("Client: RemoteException: " + ex.getMessage());
        }
        catch(UnknownHostException ex)
        {
            System.out.println("Client: Unknown host");
        }
        return ret;
    }

    /**
     * Gets the lobbies available
     * @return A list containing all available lobbies
     */
    public List<Lobby> getLobbies()
    {
        try
        {
            return lobbyAdmin.getLobbies();
        }
        catch (RemoteException ex)
        {
            System.out.println("Client: RemoteException: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Gets the next lobby id available
     * @return the next lobby id available
     */
    public int getNextID()
    {
        int i = LobbyAdmin.getNextID();
        System.out.println(i);
        return i;
    }

    /**
     * Makes the current player join a lobby
     * @param lobby that the current player will join
     * @return true if joining the lobby succeeded, false if joining the lobby failed
     */
    public boolean joinLobby(Lobby lobby, Administration admin)
    {
        boolean ret = false;
        try
        {
            if(lobbyAdmin.joinLobby(lobby, user))
            {
                System.out.print("Subscribing on ID: ");
                System.out.println(lobby.getId());
                remotePublisherForListener.subscribeRemoteListener(admin, Integer.toString(lobby.getId()));
                ret = true;
            }
            return ret;
        }
        catch(RemoteException ex){
            System.out.println("Client: RemoteException: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Gets the currently active lobby
     * @return the currently active lobby
     */
    public Lobby getActiveLobby()
    {
        try
        {
            return lobbyAdmin.getActiveLobby(user.getID());
        }
        catch(RemoteException ex)
        {
            System.out.println("Client: RemoteException: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Sets the active lobby of the specified user
     * @param lobby which will be set active
     * @param userId the user who's active lobby has to be set
     */
    public void setActiveLobby(Lobby lobby, int userId)
    {
        try
        {
            lobbyAdmin.setActiveLobby(userId, lobby);
        }
        catch(RemoteException ex)
        {
            System.out.println("Client: RemoteException: " + ex.getMessage());
        }
    }

    /**
     * Makes the specified player leave the specified lobbyId
     * @param lobbyId of the lobby
     * @param userId of the user
     * @return true if the specified player left the specified lobby, false if the specified player failed to leave the specified lobby
     */
    public void leaveLobby(int lobbyId, int userId)
    {
        try
        {
            if(lobbyAdmin.leaveLobby(lobbyId, userId, this.user.getID()))
            {
                setActiveLobby(null, userId);
            }
        }
        catch(RemoteException ex)
        {
            System.out.println("Client: RemoteException: " + ex.getMessage());
        }
    }

    /**
     * Tells the lobby to start connecting to the game
     * @param l to be informed
     */
    public void startConnectingToGame(Lobby l)
    {
        try
        {
            System.out.println("starting (lc)");
            System.out.println(l.getId());
            lobbyAdmin.startGame(l);
        }
        catch(RemoteException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Test RMI connection
     */
    private void testConnection()
    {
        try
        {
            lobbyAdmin.getNumberOfLobbies();
            System.out.println("Client: Connected ");
        }
        catch (RemoteException ex)
        {
            System.out.println("Client: Cannot connect");
            System.out.println("Client: RemoteException: " + ex.getMessage());
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
            e.printStackTrace();
        }

        return properties;
    }

    /**
     * Sets the color of the character
     * @param userColor to be set
     */
    public void setUserColor(CharacterColor userColor)
    {
        try
        {
            lobbyAdmin.setUserColor(user.getID(), userColor);
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
}
