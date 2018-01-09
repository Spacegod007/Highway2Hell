package logic.remote_method_invocation;

import logic.Gamerule;
import logic.administration.Lobby;
import logic.game.Direction;
import logic.game.GameObject;
import logic.game.ObstacleObject;
import logic.game.PlayerObject;

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

    //todo add Game methods
    List<GameObject> getGameObjects() throws RemoteException;
    List<Gamerule> getGameRules() throws RemoteException;
    List<PlayerObject> endGame() throws RemoteException;
    PlayerObject moveCharacter(String playername, Direction direction) throws RemoteException;
    List<PlayerObject> returnPlayerObjects() throws RemoteException;
    List<ObstacleObject> returnObstacleObjects() throws RemoteException;
    void startGame() throws RemoteException;
}
