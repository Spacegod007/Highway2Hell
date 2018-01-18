package logic.administration;

import javafx.collections.FXCollections;
import logic.fontyspublisher.IRemotePropertyListener;
import logic.fontyspublisher.IRemotePublisherForListener;
import logic.game.CharacterColor;
import logic.remote.method.invocation.IGameAdmin;
import logic.remote.method.invocation.LobbyAdmin;
import logic.remote.method.invocation.RMIGameClient;
import logic.remote.method.invocation.RMILobbyClient;
import sample.SampleMain;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An administration class which is used to interact with the server
 */
public class Administration extends UnicastRemoteObject implements IRemotePropertyListener
{
    private static final Logger LOGGER = Logger.getLogger(Administration.class.getName());

    /**
     * The client which interacts with the server
     */
    private final transient RMILobbyClient rmiClient;

    /**
     * The client which interacts with the game server
     */
    private RMIGameClient rmiGameClient;

    /**
     * The thread the game server will be run on should the local user be the host
     */
    private transient Thread gameThread;

    /**
     * The adminstration which manges the server-side (host) of the game
     */
    private transient HostAdministration hostAdministration;

    /**
     * The user on who's behalf interactions will happen
     */
    private User user;

    /**
     * The view where the effect of the actions in this class will be shown
     */
    private transient SampleMain sampleMain;

    /**
     * The constructor of the administration object
     * @param rmiClient which is used to connect to the server
     * @throws RemoteException if there is an error within the rmiClient
     */
    public Administration(RMILobbyClient rmiClient) throws RemoteException
    {
        this.rmiClient = rmiClient;
        this.user = rmiClient.getUser();

        try
        {
            //subscribe to the server as a listener if there are any updates with lobbies
            rmiClient.getRpl().subscribeRemoteListener(this, LobbyAdmin.LOBBIES_PROPERTY);
        }
        catch(RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "Error in connection", ex);
        }
    }

    /**
     * Gets the game admin
     * @return IGameAdmin object
     */
    public IGameAdmin getGameAdmin()
    {
        return rmiGameClient.getGameAdmin();
    }

    /**
     * Sets the username of the user
     * @param username the new username of the user
     */
    public boolean setUsername(String username)
    {
        User preUser = rmiClient.setUsername(username);

        if (preUser != null)
        {
            this.setUser(preUser);
            sampleMain.setLvLobby(FXCollections.observableList(rmiClient.getLobbies()));
            return true;
        }

        return false;
    }

    /**
     * Checks if the user is in a lobby
     * @return true if the user is in a lobby, false if he is not in a lobby
     */
    public boolean inLobby()
    {
        return rmiClient.getActiveLobby() != null;
    }

    /**
     * Gets the user object
     * @return the current user object
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user object
     * @param user to be set
     */
    private void setUser(User user)
    {
        this.user = user;
    }

    /**
     * Join the specified lobby
     * @param lobby to be joined
     * @return true if joining succeeded, false if joining failed
     */
    public boolean joinLobby(Lobby lobby){
        try
        {
            if(rmiClient.joinLobby(lobby, this))
            {
                rmiClient.setActiveLobby(lobby, rmiClient.getUser().getId());
                return true;
            }
            return false;
        }
        catch(Exception e)
        {
            LOGGER.log(Level.SEVERE, "Error joining lobby", e);
            return false;
        }
    }

    /**
     * Leave the current lobby the user is located in
     * @return true if leaving succeeded, false if leaving failed
     */
    public void leaveLobby()
    {
        leaveLobby(rmiClient.getUser().getId());
    }

    /**
     * Sends a request to the server to make the specified user leave the lobby
     * @param leaverId the id of the user which will leave the server
     * @return true if leaving succeeded, false if leaving failed
     */
    public void leaveLobby(int leaverId)
    {
            Lobby lobby = rmiClient.getActiveLobby();

            if (lobby != null)
            {
                rmiClient.leaveLobby(lobby.getId(), leaverId);
            }
            else
            {
                LOGGER.log(Level.SEVERE, "Lobby is null");
            }
    }

    /**
     * Host a new lobby with the specified name
     * @param name of the new lobby
     * @return the lobby which was created and now being hosted on the server
     */
    public Lobby hostLobby(String name)
    {
        Lobby lobby = null;

        try{
            lobby = rmiClient.addLobby(name, this);

            if (lobby != null)
            {
                rmiClient.setActiveLobby(lobby, rmiClient.getUser().getId());
            }

            return lobby;
        }
        catch (Exception e)
        {
            return lobby;
        }
    }

    /**
     * Updates available lobbies
     * @param evt property which got changed with changes
     */
    private void setListViewLobby(PropertyChangeEvent evt)
    {
        sampleMain.setLvLobby(FXCollections.observableList((List<Lobby>) evt.getNewValue()));
        String propertyChanged = String.format("property changed: %s", evt.getPropertyName());
        LOGGER.log(Level.INFO, propertyChanged);
    }

    /**
     * Is called whenever a player connects
     * @param evt property which got changed with changes
     */
    private void playerConnected(PropertyChangeEvent evt)
    {
        String playersConnected = String.format("players connected: %s", evt.getNewValue());
        LOGGER.log(Level.INFO, playersConnected);
        int waitingPlayers = (rmiClient.getActiveLobby().getPlayers().size()) - (int) evt.getNewValue();
        sampleMain.setWaitingPlayers(waitingPlayers);

        if (waitingPlayers <= 0 && hostAdministration != null && rmiGameClient != null)
        {
            rmiGameClient.gameIsStarted();
        }
    }

    /**
     * marks the game has started
     */
    private void gameIsStarted(PropertyChangeEvent evt)
    {
        LOGGER.log(Level.INFO, "game is started");
        sampleMain.update(evt.getNewValue());
    }

    private void startConnecting(PropertyChangeEvent evt)
    {
        if(rmiGameClient == null)
        {
            rmiGameClient = new RMIGameClient(rmiClient.getActiveLobby().getIpAddress(), this);
        }

        sampleMain.setWaitingScreen();
        sampleMain.setWaitingPlayers((int)evt.getNewValue());
        LOGGER.log(Level.INFO, "lobby started");
    }

    /**
     * Triggers when the list of lobbies changed, should not be used manually
     * @param evt PropertyChangeEvent @see java.beans.PropertyChangeEvent
     * @throws RemoteException if there's a problem with the connection
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        switch (evt.getPropertyName())
        {
            case LobbyAdmin.LOBBIES_PROPERTY:
                setListViewLobby(evt);
                return;
            case "playersconnected":
                playerConnected(evt);
                return;
            case "gameIsStarted":
                gameIsStarted(evt);
                return;
            default:
                //This property initiates clients connecting to the game server
                if(evt.getPropertyName().equals(Integer.toString(rmiClient.getActiveLobby().getId())))
                {
                    startConnecting(evt);
                }
        }
    }

    /**
     * Sets the sampleMain
     * @param sampleMain to be set
     */
    public void setSampleMain(SampleMain sampleMain)
    {
        this.sampleMain = sampleMain;
    }

    /**
     * Starts the game in a separate thread
     * Can only be called by Host
     */
    public void startConnectingToGame()
    {
        Lobby lobby = rmiClient.getActiveLobby();

        if(lobby != null)
        {
            hostAdministration = new HostAdministration(lobby);
            gameThread = new Thread(hostAdministration);

            //Starts the hostAdministration on a new thread. The only thing this class does at this point is starting the server
            gameThread.start();

            //Says to the lobby -through the client- that he can inform all his listeners to start connecting
            rmiClient.startConnectingToGame(lobby);
        }
        else
        {
            LOGGER.log(Level.WARNING, "null lobby");
        }
    }

    /**
     * Stops currently running game
     */
    public void endGame()
    {
        gameThread.stop();
        rmiGameClient = null;
    }

    /**
     * Gets the publisher for server-push methods to the client
     * @return an IRemotePublisherForListener object, which can be subscribed to and un-subscribed from
     */
    public IRemotePublisherForListener getRpl()
    {
        return rmiGameClient.getRpl();
    }

    /**
     * Checks if the current user is in a lobby
     * @return true if the user is in a lobby otherwise false
     */
    public boolean userInLobby()
    {
        return (rmiClient.getActiveLobby() != null);
    }

    /**
     * sets the color of the user-picked character
     * @param userColor the color of the user-picked character
     */
    public void setUserColor(CharacterColor userColor)
    {
        rmiClient.setUserColor(userColor);
    }

    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}
