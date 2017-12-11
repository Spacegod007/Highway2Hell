package logic.remote_method_invocation;

import logic.administration.Lobby;
import logic.administration.User;
import logic.fontyspublisher.IRemotePublisherForDomain;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * the server-side (host) of the game administration
 */
public class GameAdmin extends UnicastRemoteObject implements IGameAdmin
{

    private List<RMIGameClient> clientsConnected;
    private Lobby lobby = null;

    public void setLobby(Lobby lobby)
    {
        this.lobby = lobby;
    }

    public int getPlayersConnected()
    {
        return clientsConnected.size();
    }
    public List<RMIGameClient> getConnectedClients()
    {
        return clientsConnected;
    }

    /**
     * Remote publisher to publish the host's data to the clients
     */
    private IRemotePublisherForDomain rpd;


    /**
     * Constructs a GameAdmin object
     * @param publisher to publish the host's data to the clients
     * @throws RemoteException on errors in the connection
     */
    public GameAdmin(IRemotePublisherForDomain publisher) throws RemoteException
    {
        clientsConnected = new ArrayList<>();
        this.rpd = publisher;
        rpd.registerProperty("playersconnected");
        rpd.registerProperty("gameIsStarted");
    }

    public void connect(RMIGameClient client)
    {
        clientsConnected.add(client);
        try
        {
            rpd.inform("playersconnected", null, clientsConnected.size());
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public void gameIsStarted()
    {
        try
        {
            rpd.inform("gameIsStarted", null, toUserList(clientsConnected));
            rpd.registerProperty("gameState");
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    private List<User> toUserList(List<RMIGameClient> list)
    {
        List<User> ret = new ArrayList<>();
        for(RMIGameClient c : list)
        {
            ret.add(c.getUser());
        }
        return ret;
    }
}
