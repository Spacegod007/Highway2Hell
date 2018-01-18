package logic.remote.method.invocation;

import logic.fontyspublisher.IRemotePublisherForDomain;
import logic.administration.Lobby;
import logic.administration.User;
import logic.fontyspublisher.RemotePublisher;
import logic.game.CharacterColor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The server-side of the lobby administration
 */
public class LobbyAdmin extends UnicastRemoteObject implements ILobbyAdmin
{
    private static final Logger LOGGER = Logger.getLogger(LobbyAdmin.class.getName());

    public static final String LOBBIES_PROPERTY = "lobbies";

    /**
     * The currently active lobbies
     */
    private ArrayList<Lobby> lobbies;

    /**
     * The currently active users
     */
    private ArrayList<User> users;

    /**
     * The publisher which will notify the subscribers when a new lobby became active
     */
    private final transient IRemotePublisherForDomain rpd;

    /**
     * The next id for a lobby
     */
    private static int nextLobbyId = 0;

    /**
     * The next id for a user
     */
    private static int nextUserID = 0;

    /**
     * A synchronizer object for the lobby system
     */
    private final transient Object lobbySynchronizer = new Object();

    /**
     * Gets the next lobby Id
     * @return the next lobby id
     */
    static int getNextID()
    {
        int i = nextLobbyId;
        nextLobbyId++;
        return i;
    }

    /**
     * Gets the next user Id
     * @return the next user id
     */
    private static int getNextUserID()
    {
        int i = nextUserID;
        nextUserID++;
        return i;
    }

    /**
     * Constructor of the server-side lobby administration
     * @param rpd to publish to subscribers
     * @throws RemoteException if there is a problem in the connection
     */
    public LobbyAdmin(IRemotePublisherForDomain rpd) throws RemoteException {
        super();
        this.rpd = rpd;
        rpd.registerProperty(LOBBIES_PROPERTY);
        lobbies = new ArrayList<>();
        users = new ArrayList<>();
    }

    /**
     * Gets the number of lobbies available
     * @return the number of lobbies
     * @throws RemoteException if there is a problem in the connection
     */
    public void getNumberOfLobbies() throws RemoteException {
        LOGGER.log(Level.INFO, "LobbyAdmin: Request for number of lobbies");
        lobbies.size();
    }

    /**
     * Adds a lobby to the system
     * @param user user that created the lobby (aka host)
     * @param ipAddress of the user who will run the server later
     * @return The newly created lobby
     * @throws RemoteException if there is a problem in the connection
     */
    public Lobby addLobby(String name, User user, String ipAddress) throws RemoteException {
        Lobby lobby = new Lobby(name, getNextID(), ipAddress);
        synchronized (lobbySynchronizer)
        {
            lobbies.add(lobby);
            rpd.registerProperty(Integer.toString(lobby.getId()));
            joinLobby(lobby, user);
        }
        String lobbyAddedMessage = String.format("LobbyAdmin: Lobby %s added to Lobby administrator", lobby.toString());
        LOGGER.log(Level.INFO, lobbyAddedMessage);
        rpd.inform(LOBBIES_PROPERTY, null, lobbies);
        return lobby;
    }

    /**
     * Makes a user leave a lobby
     * @param userId of the user who will leave the lobby
     * @param issuerId of the user who made the other user leave the lobby
     * @return true if the user left the lobby, false if leaving the lobby failed
     * @throws RemoteException if there is a problem in the connection
     */
    public boolean leaveLobby(int lobbyId, int userId, int issuerId) throws RemoteException
    {
        synchronized (lobbySynchronizer)
        {
            try
            {
                for (Lobby l : lobbies)
                {
                    if (l.getId() == lobbyId && (userId == issuerId || issuerId == l.getHost().getId()))
                    {
                        l.leave(userId);
                        if (userId == l.getHost().getId()) //if the leaver is the host
                        {
                            if (l.getPlayers().size() > 0) //and there are players remaining
                            {
                                l.setHost(l.getPlayers().get(0)); //migrate host
                            }
                            else
                            {
                                l.setHost(null); //else, set host to null, lobby will be removed by the next tick of the timer
                            }
                        }
                        rpd.inform(LOBBIES_PROPERTY, null, lobbies);
                        return true;
                    }
                }
            }
            catch(RemoteException ex)
            {
                LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, ex);
            }
        }
        return false;
    }

    /**
     * Gets a list of all the lobbies
     * @return a list of all the lobbies
     * @throws RemoteException if there is a problem in the connection
     */
    public List<Lobby> getLobbies() throws RemoteException
    {
        return lobbies;
    }

    /**
     * Makes a user join a lobby
     * @param lobby to be joined
     * @param user to join the lobby
     * @return true if the user succeeded joining, false if the user failed to join
     * @throws RemoteException if there is a problem in the connection
     */
    public boolean joinLobby(Lobby lobby, User user) throws RemoteException
    {
        boolean ret = false;
        synchronized (lobbySynchronizer)
        {
            for (Lobby l : lobbies)
            {
                if (l.getId() == lobby.getId())
                {
                    try
                    {
                        rpd.inform(LOBBIES_PROPERTY, null, lobbies);
                    }
                    catch(RemoteException ex)
                    {
                        LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, ex);
                    }

                    if(l.join(user))
                    {
                        ret = true;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Sets the active lobby of the specified user
     * @param userId of the user
     * @param lobby which will be set as active lobby
     * @return the lobby which was set active
     * @throws RemoteException if there is a problem in the connection
     */
    public void setActiveLobby(int userId, Lobby lobby) throws RemoteException
    {
        String settingLobbyMessage = String.format("setting lobby for user: %s", userId);
        LOGGER.log(Level.INFO, settingLobbyMessage);
        User u = getUser(userId);
        if(u != null)
        {
                u.setActiveLobby(getLobby(lobby));
        }
        getActiveLobby(userId);
    }

    /**
     * Gets the specified user by id
     * @param id of the user
     * @return the user object linked to the id
     */
    private User getUser(int id)
    {
        User l = null;
        for(User user : users)
        {
            if(id == user.getId())
            {
                l = user;
            }
        }
        return l;
    }

    /**
     * Renews the information in a lobby object
     * @param lob to be renewed
     * @return the renewed lobby object
     */
    private Lobby getLobby(Lobby lob)
    {
        if(lob != null)
        {
            for (Lobby lobby : lobbies)
            {
                if (lobby.getId() == lob.getId())
                {
                    return lobby;
                }
            }
        }
        return null;
    }

    /**
     * Adds a user to the list of users
     * @param username of the user
     * @return A user object containing the name of the user
     * @throws RemoteException if there is a problem in the connection
     */
    public User addUser(String username) throws RemoteException
    {
        for (User u : users)
        {
            if (u.getUsername().equalsIgnoreCase(username))
            {
                return null;
            }
        }

        User user = new User(username, getNextUserID());
        users.add(user);
        return user;
    }

    /**
     * Removes the unused lobbies
     */
    public void cleanLobbies()
    {
        synchronized (lobbySynchronizer)
        {
            boolean changed = false;

            for (int i = 0; i < lobbies.size();)
            {
                if (lobbies.get(i).getPlayers().size() == 0)
                {
                    lobbies.remove(i);
                    changed = true;
                }
                else
                {
                    i++;
                }
            }

            if(changed){
                try
                {
                    rpd.inform(LOBBIES_PROPERTY, null, lobbies);
                }
                catch (RemoteException ex)
                {
                    LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, ex);
                }
            }

        }
    }

    /**
     * Gets the active lobby of the specified user
     * @param userId who's lobby is requested
     * @return the lobby the user is located in
     * @throws RemoteException if there is a problem in the connection
     */
    public Lobby getActiveLobby(int userId) throws RemoteException
    {
        User u = getUser(userId);
        if(u != null)
        {
            return u.getActiveLobby();
        }
        return null;
    }

    /**
     * Starts the game of the specified lobby
     * @param l where the game is being started from
     */
    public void startGame(Lobby l)
    {
        try
        {
            rpd.inform(Integer.toString(l.getId()), null, l.getPlayers().size());
        }
        catch (RemoteException e)
        {
            LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, e);
        }
    }

    @Override
    public void setUserColor(int id, CharacterColor userColor)
    {
        User u = getUser(id);
        if(u != null)
        {
            for (User us : u.getActiveLobby().getPlayers())
            {
                if (us.getId() == id)
                {
                    us.setCharacterColor(userColor);
                }
            }

            try
            {
                rpd.inform(LOBBIES_PROPERTY, null, lobbies);
            }
            catch (RemoteException ex)
            {
                LOGGER.log(Level.SEVERE, RemotePublisher.ERROR_MESSAGE, ex);
            }
        }
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
