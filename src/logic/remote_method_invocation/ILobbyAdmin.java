package logic.remote_method_invocation;

import logic.administration.Lobby;
import logic.administration.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ILobbyAdmin extends Remote {
    int getNumberOfLobbies() throws RemoteException;
    Lobby getActiveLobby(int userId) throws RemoteException;
    Lobby addLobby(String lobby, User user, String ipAddress) throws RemoteException;
    List<Lobby> getLobbies() throws RemoteException;
    boolean joinLobby(Lobby lobby, User user) throws RemoteException;
    boolean leaveLobby(int lobby, int userId, int issuerId) throws RemoteException;
    Lobby setActiveLobby(int userId, Lobby lobby) throws RemoteException;
    User addUser(String name) throws RemoteException;
}
