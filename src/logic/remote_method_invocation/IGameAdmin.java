package logic.remote_method_invocation;

import logic.administration.Lobby;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The allowed methods for interaction on the GameAdmin
 */
public interface IGameAdmin extends Remote
{
    void connect(RMIGameClient client) throws RemoteException;
    int getPlayersConnected() throws RemoteException;
    void setLobby(Lobby lobby) throws RemoteException;
    List<RMIGameClient> getConnectedClients() throws RemoteException;
    void gameIsStarted() throws RemoteException;
}
