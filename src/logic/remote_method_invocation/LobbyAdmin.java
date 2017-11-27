package logic.remote_method_invocation;

import logic.fontyspublisher.IRemotePublisherForDomain;
import logic.administration.Lobby;
import logic.administration.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class LobbyAdmin extends UnicastRemoteObject implements ILobbyAdmin{
    private ArrayList<Lobby> lobbies;
    private ArrayList<User> users;
    private IRemotePublisherForDomain rpd;
    private static int nextID = 0;
    private static int nextUserID = 0;

    private final Object lobbiesynchronizer = new Object();

    static int getNextID(){
        int i = nextID;
        nextID++;
        return i;
    }
    static int getNextUserID(){
        int i = nextUserID;
        nextUserID++;
        return i;
    }

    // Constructor
    public LobbyAdmin(IRemotePublisherForDomain rpd) throws RemoteException {
        this.rpd = rpd;
        rpd.registerProperty("lobbies");
        lobbies = new ArrayList<>();
        users = new ArrayList<>();
    }

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

    public List<Lobby> getLobbies() throws RemoteException
    {
        return lobbies;
    }

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
