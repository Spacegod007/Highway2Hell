package logic.remote_method_invocation;

import logic.administration.Lobby;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The allowed methods for interaction on the GameAdmin
 */
public interface IGameAdmin extends Remote
{
    void connect(RMIGameClient client) throws RemoteException;
    int getPlayersConnected() throws RemoteException;
    void setLobby(Lobby lobby) throws RemoteException;
}
