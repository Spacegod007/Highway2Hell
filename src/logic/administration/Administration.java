package logic.administration;

import javafx.collections.FXCollections;
import logic.fontyspublisher.IRemotePropertyListener;
import logic.remote_method_invocation.RMIGameClient;
import logic.remote_method_invocation.RMILobbyClient;
import sample.SampleMain;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * An administration class which is used to interact with the server
 */
public class Administration extends UnicastRemoteObject implements IRemotePropertyListener
{
    /**
     * The client which interacts with the server
     */
    private RMILobbyClient rmiClient;
    private RMIGameClient rmiGameClient;
    private Thread gameThread;
    private HostAdministration hostAdministration;

    /**
     * The user on who's behalf interactions will happen
     */
    private User user;

    /**
     * The view where the effect of the actions in this class will be shown
     */
    private SampleMain sampleMain;

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
            rmiClient.getRpl().subscribeRemoteListener(this, "lobbies");
        }
        catch(RemoteException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Sets the username of the user
     * @param username
     */
    public boolean setUsername(String username)
    {
        User preUser = rmiClient.setUsername(username);

        if (preUser != null)
        {
            this.setUser(preUser);
            sampleMain.setListvwLobby(FXCollections.observableList(rmiClient.getLobbies()));
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
    public void setUser(User user)
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
                rmiClient.setActiveLobby(lobby, rmiClient.getUser().getID());
                return true;
            }
            return false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Leave the current lobby the user is located in
     * @return true if leaving succeeded, false if leaving failed
     */
    public boolean leaveLobby()
    {
        return leaveLobby(rmiClient.getUser().getID());
    }

    /**
     * Sends a request to the server to make the specified user leave the lobby
     * @param leaverId the id of the user which will leave the server
     * @return true if leaving succeeded, false if leaving failed
     */
    public boolean leaveLobby(int leaverId)
    {
        try{
            Lobby lobby = rmiClient.getActiveLobby();

            if (lobby != null)
            {
                if (rmiClient.leaveLobby(lobby.getId(), leaverId))
                {
                    return true;
                }
            }
            else
            {
                System.out.println("Error: Lobby is null");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return false;
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
                rmiClient.setActiveLobby(lobby, rmiClient.getUser().getID());
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
        sampleMain.setListvwLobby(FXCollections.observableList((List<Lobby>) evt.getNewValue()));
        System.out.println("property changed: " + evt.getPropertyName());
    }

    /**
     * Is called whenever a player connects
     * @param evt property which got changed with changes
     */
    private void playerConnected(PropertyChangeEvent evt)
    {
        System.out.println("playersconnected: " + evt.getNewValue());
        int waitingPlayers = (rmiClient.getActiveLobby().getPlayers().size()) - (int) evt.getNewValue();
        sampleMain.setWaitingPlayers(waitingPlayers);

        if (waitingPlayers <= 0)
        {
            //Dit wordt alleen op de host gedaan nu, want dat is de enige met en hostAdministration
            if(hostAdministration != null && rmiGameClient != null)
            {
                //add parameters
                //startGame would mean that the game shows up and starts running, everyone was already connected at this point.
                //TODO this makes actually no sense because only the host would be starting the game

                //hostAdministration.startGame(rmiGameClient.getConnectedClients());
                rmiGameClient.gameIsStarted();
            }
        }
    }

    /**
     * marks the game has started
     */
    private void gameIsStarted(PropertyChangeEvent evt)
    {
        System.out.println("game is started");
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
        System.out.println("lobby started");
    }

    /**
     * Triggers when the list of lobbies changed, should not be used manually
     * @param evt PropertyChangeEvent @see java.beans.PropertyChangeEvent
     * @throws RemoteException
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        switch (evt.getPropertyName())
        {
            case "lobbies":
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
        System.out.println("starting");
        Lobby lobby = rmiClient.getActiveLobby();

        if(lobby != null)
        {
            try
            {
                hostAdministration = new HostAdministration(lobby);
                gameThread = new Thread(hostAdministration);

                //Starts the hostAdministration on a new thread. The only thing this class does at this point is starting the server
                gameThread.start();
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }

            //Says to the lobby -through the client- that he can inform all his listeners to start connecting
            rmiClient.startConnectingToGame(lobby);
        }
        else
        {
            System.out.println("null lobby");
        }
    }
}
