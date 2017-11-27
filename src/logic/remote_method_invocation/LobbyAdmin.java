package logic.remote_method_invocation;

import logic.fontyspublisher.IRemotePublisherForDomain;
import logic.administration.Lobby;
import logic.administration.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * The server-side of the lobby administration
 */
public class LobbyAdmin extends UnicastRemoteObject implements ILobbyAdmin
{
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
    private IRemotePublisherForDomain rpd;

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
    private final Object lobbiesynchronizer = new Object();

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
    static int getNextUserID()
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
        this.rpd = rpd;
        rpd.registerProperty("lobbies");
        lobbies = new ArrayList<>();
        users = new ArrayList<>();
    }

    /**
     * Gets the number of lobbies available
     * @return the number of lobbies
     * @throws RemoteException if there is a problem in the connection
     */
    public int getNumberOfLobbies() throws RemoteException {
        System.out.println("LobbyAdmin: Request for number of Lobbies");
        return lobbies.size();
    }

    public Lobby getLobby(int nr) throws RemoteException {
        System.out.println("LobbyAdmin: Request for Lobby with number " + nr);
        if (nr >= 0 && nr < lobbies.size()) {
            return lobbies.get(nr);
        }
        else {
            return null;
        }
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
        synchronized (lobbiesynchronizer)
        {
            lobbies.add(lobby);
            joinLobby(lobby, user);
        }
        System.out.println("LobbyAdmin: Lobby " + lobby.toString() + " added to Lobby administration");
        rpd.inform("lobbies", null, lobbies);
        return lobby;
    }

    /**
     * Makes a user leave a lobby
     * @param userId of the user who will leave the lobby
     * @param issuerId of the user who made the other user leave the lobby
     * @return true if the user left the lobby, false if leaving the lobby failed
     * @throws RemoteException if there is a problem in the connection
     */
    public boolean leaveLobby(int lobbyId, int userId, int issuerId)
    {
        synchronized (lobbiesynchronizer)
        {
            try
            {
                for (Lobby l : lobbies)
                {
                    if (l.getId() == lobbyId) //find matching lobby
                    {
                        if (userId == issuerId || issuerId == l.getHost().getID()) //if host or self-leave
                        {
                            l.leave(userId);
                            if (userId == l.getHost().getID()) //if the leaver is the host
                            {
                                if (l.getPlayers().size() > 0) //and there are players remaining
                                {
                                    l.setHost(l.getPlayers().get(0)); //migrate host
                                } else
                                {
                                    l.setHost(null); //else, set host to null, lobby will be removed by the next tick of the timer
                                }
                            }
                            rpd.inform("lobbies", null, lobbies);
                            return true;
                        }
                    }
                }
            }
            catch(RemoteException ex)
            {
                ex.printStackTrace();
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
    public boolean joinLobby(Lobby lobby, User user)
    {
        synchronized (lobbiesynchronizer)
        {
            for (Lobby l : lobbies)
            {
                if (l.getId() == lobby.getId())
                {
                    try{rpd.inform("lobbies", null, lobbies);}
                    catch(RemoteException ex){ex.printStackTrace();}
                    return l.join(user);
                }
            }
        }
        return false;
    }

    /**
     * Sets the active lobby of the specified user
     * @param userId of the user
     * @param lobby which will be set as active lobby
     * @return the lobby which was set active
     * @throws RemoteException if there is a problem in the connection
     */
    public Lobby setActiveLobby(int userId, Lobby lobby)
    {
        if(lobby != null)
        {
            for(Lobby l : lobbies){
                if(l.getId() == lobby.getId())
                {  for(User u : users){
                    if(u.getID() == userId)
                    {
                        u.setActiveLobby(l);
                    }
                }
                }
            }
        }
        else{
            for(User u : users){
                if(u.getID() == userId)
                {
                    u.setActiveLobby(null);
                }
            }
        }
        return getActiveLobby(userId);
    }

    /**
     * Adds a user to the list of users
     * @param username of the user
     * @return A user object containing the name of the user
     * @throws RemoteException if there is a problem in the connection
     */
    public User addUser(String username)
    {
        User user = new User(username, getNextUserID());
        users.add(user);
        return user;
    }

    public void cleanLobbies()
    {
        synchronized (lobbiesynchronizer)
        {
            boolean changed = false;
            for (int i = 0; i < lobbies.size(); i++)
            {
                if (lobbies.get(i).getPlayers().size() == 0)
                {

                    lobbies.remove(i);
                    changed = true;
                    i--;
                }
            }
            if(changed){
                try
                {
                    rpd.inform("lobbies", null, lobbies);
                }
                catch(RemoteException ex){ex.printStackTrace();}
            }

        }
    }

    /**
     * Gets the active lobby of the specified user
     * @param userId who's lobby is requested
     * @return the lobby the user is located in
     * @throws RemoteException if there is a problem in the connection
     */
    public Lobby getActiveLobby(int userId)
    {
        for(User u : users)
        {
            if(u.getID() == userId)
            {
                return u.getActiveLobby();
            }
        }
        return null;
    }
}
