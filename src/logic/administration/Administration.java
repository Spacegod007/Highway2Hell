package logic.administration;

import logic.fontyspublisher.IRemotePropertyListener;
import logic.remote_method_invocation.RMIGameClient;
import logic.remote_method_invocation.RMILobbyClient;
import javafx.collections.FXCollections;
import sample.Main;
import javafx.stage.Stage;
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
    private AdministrationGame administrationGame;

    /**
     * The user on who's behalf interactions will happen
     */
    private User user;

    /**
     * The view where the effect of the actions in this class will be shown
     */
    private Main main;

    /**
     * The constructor of the administration object
     * @param rmiClient which is used to connect to the server
     * @throws RemoteException if there is an error within the rmiClient
     */
    public Administration(RMILobbyClient rmiClient) throws RemoteException{
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
    public void setUsername(String username)
    {
        this.setUser(rmiClient.setUsername(username));
        main.setListvwLobby(FXCollections.observableList(rmiClient.getLobbies()));
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
     * Triggers when the list of lobbies changed, should not be used manually
     * @param evt PropertyChangeEvent @see java.beans.PropertyChangeEvent
     * @throws RemoteException
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        if(evt.getPropertyName().equals("lobbies"))
        {
            main.setListvwLobby(FXCollections.observableList((List<Lobby>) evt.getNewValue()));
            System.out.println("property changed: " + evt.getPropertyName());
            return;
        }
        if(evt.getPropertyName().equals(Integer.toString(rmiClient.getActiveLobby().getId())))
        {
            if(rmiGameClient == null)
            {
                rmiGameClient = new RMIGameClient(rmiClient.getActiveLobby().getIpAddress(), this);
            }

            main.setWaitingScreen();
            main.setWaitingPlayers((int)evt.getNewValue());
            System.out.println("lobby started");
        }
        if(evt.getPropertyName().equals("playersconnected"))
        {
                System.out.println("playersconnected: " + evt.getNewValue());
                int waitingPlayers = (rmiClient.getActiveLobby().getPlayers().size()) - (int) evt.getNewValue();
                main.setWaitingPlayers(waitingPlayers);
                if (waitingPlayers <= 0)
                {
                    //Dit wordt alleen op de host gedaan nu, want dat is de enige met en administrationGame
                    if(administrationGame != null)
                    {
                        //add parameters
                        administrationGame.startGame();
                    }
                }
        }
    }

    /**
     * Sets the main
     * @param main to be set
     */
    public void setMain(Main main)
    {
        this.main = main;
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
                administrationGame = new AdministrationGame(lobby);
                gameThread = new Thread(administrationGame);
                gameThread.start();
            } catch (RemoteException e)
            {
                e.printStackTrace();
            }
            rmiClient.startConnectingToGame(lobby);
        }
        else
            {
                System.out.println("null lobby");
            }
    }
}
