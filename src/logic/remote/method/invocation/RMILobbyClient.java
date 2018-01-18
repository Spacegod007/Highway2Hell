/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.remote.method.invocation;

import logic.administration.Administration;
import logic.fontyspublisher.IRemotePublisherForListener;
import logic.administration.Lobby;
import logic.administration.User;
import logic.fontyspublisher.RemotePublisher;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The client for RMI
 */
public class RMILobbyClient
{
    private static final Logger LOGGER = Logger.getLogger(RMILobbyClient.class.getName());

    /**
     * The binding name for the administration
     */
    private static final String BINDING_NAME = "LobbyAdmin";

    /**
     * The binding name for the publisher
     */
    private static final String BINDING_NAME_PUBLISHER = "publisher";

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
        String ipAddressMessage = String.format("LobbyClient: ip address %s", ipAddress);
        LOGGER.log(Level.INFO, ipAddressMessage);
        String portNumberMessage = String.format("LobbyClient: port number %s", portNumber);
        LOGGER.log(Level.INFO, portNumberMessage);

        // Locate registry at IP address and port number
        try {
            registry = LocateRegistry.getRegistry(ipAddress, portNumber);
        } catch (RemoteException ex) {
            LOGGER.log(Level.SEVERE, "LobbyClient: cannot locate registry, remote exception", ex);
            registry = null;
        }

        // Print result locating registry
        if (registry != null) {
            LOGGER.log(Level.INFO, "LobbyClient: registry located");
        } else {
            LOGGER.log(Level.WARNING, "LobbyClient: cannot locate registry");
            LOGGER.log(Level.WARNING, "LobbyClient: registry is null pointer");
        }

        // Print contents of registry
        if (registry != null) {
            printContentsRegistry();
        }

        // Bind student administration using registry
        if (registry != null) {
            try
            {
                lobbyAdmin = (ILobbyAdmin) registry.lookup(BINDING_NAME);
            }
            catch (RemoteException ex)
            {
                LOGGER.log(Level.SEVERE, "LobbyClient: cannot bind lobby administration, remote exception", ex);
                lobbyAdmin = null;
            }
            catch (NotBoundException ex)
            {
                LOGGER.log(Level.SEVERE, "LobbyClient: cannot bind lobby administration, not bound exception", ex);
                lobbyAdmin = null;
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
                LOGGER.log(Level.SEVERE, "LobbyClient: cannot bind lobby administration, remote exception", ex);
                remotePublisherForListener = null;
            }
            catch (NotBoundException ex)
            {
                LOGGER.log(Level.SEVERE, "LobbyClient: cannot bind lobby administration, not bound exception", ex);
                remotePublisherForListener = null;
            }
        }

        // Print result binding student administration
        if (lobbyAdmin != null)
        {
            LOGGER.log(Level.INFO, "LobbyClient: lobby administration bound");
        }
        else
        {
            LOGGER.log(Level.WARNING, "LobbyClient: lobby administration is null pointer");
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
            LOGGER.log(Level.SEVERE, "LobbyClient: user not set");
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
            LOGGER.log(Level.INFO, "LobbyClient: list of names bound in registry:");

            if (listOfNames.length != 0)
            {
                for (String s : registry.list())
                {
                    LOGGER.log(Level.INFO, s);
                }
            }
            else
            {
                LOGGER.log(Level.WARNING, "LobbyClient: list of names bound in registry is empty");
            }
        }
        catch (RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "LobbyClient: cannot show list of names bound in registry, remote exception", ex);
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
                String subscribingOnIdMessage = String.format("Subscribing on id: %s", ret.getId());
                LOGGER.log(Level.INFO, subscribingOnIdMessage);
                remotePublisherForListener.subscribeRemoteListener(admin, Integer.toString(ret.getId()));
            }
        }
        catch(RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, getRemoteExceptionMessage(), ex);
        }
        catch(UnknownHostException ex)
        {
            LOGGER.log(Level.SEVERE, "LobbyClient: unknown host", ex);
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
            LOGGER.log(Level.SEVERE, getRemoteExceptionMessage(), ex);
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
        String nextIdMessage = String.valueOf(i);
        LOGGER.log(Level.INFO, nextIdMessage);
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
                String subscribingOnIdMessage = String.format("Subscribing on id: %s", lobby.getId());
                LOGGER.log(Level.INFO, subscribingOnIdMessage);
                remotePublisherForListener.subscribeRemoteListener(admin, Integer.toString(lobby.getId()));
                ret = true;
            }
            return ret;
        }
        catch(RemoteException ex){
            LOGGER.log(Level.SEVERE, getRemoteExceptionMessage(), ex);
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
            return lobbyAdmin.getActiveLobby(user.getId());
        }
        catch(RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, getRemoteExceptionMessage(), ex);
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
            LOGGER.log(Level.SEVERE, getRemoteExceptionMessage(), ex);
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
            if(lobbyAdmin.leaveLobby(lobbyId, userId, this.user.getId()))
            {
                setActiveLobby(null, userId);
            }
        }
        catch(RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, getRemoteExceptionMessage(), ex);
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
            String startingLobbyClientMessage = String.format("starting (lc) %s", l.getId());
            LOGGER.log(Level.INFO, startingLobbyClientMessage);
            lobbyAdmin.startGame(l);
        }
        catch(RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "Error in connection", ex);
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
            LOGGER.log(Level.INFO, "LobbyClient: Connected");
        }
        catch (RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "LobbyClient: cannot connect");
            LOGGER.log(Level.SEVERE, getRemoteExceptionMessage(), ex);
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
            LOGGER.log(Level.CONFIG, "Error in loading properties file", e);
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
            lobbyAdmin.setUserColor(user.getId(), userColor);
        } catch (RemoteException e)
        {
            LOGGER.log(Level.SEVERE, "Error in connection", e);
        }
    }

    private String getRemoteExceptionMessage()
    {
        return String.format("LobbyClient: %s", RemotePublisher.ERROR_MESSAGE);
    }
}
