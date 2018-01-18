package logic.remote.method.invocation;

import logic.Gamerule;
import logic.administration.User;
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
    /**
     * the method used to connect to the game server
     * @param client which connected to the game server
     * @return the session id linked to this user
     * @throws RemoteException if something goes wrong in the connection
     */
    long connect(User client) throws RemoteException;

    /**
     * Gets the amount of players currently connected
     * @return an integer value containing the amount of connected players
     * @throws RemoteException if something goes wrong in the connection
     */
    int getPlayersConnected() throws RemoteException;

    /**
     * Gets the list of players
     * @return a list containing user objects which identify the players
     * @throws RemoteException if something goes wrong in the connection
     */
    List<User> getPlayerlist() throws RemoteException;

    /**
     * Informs clients the game has started
     * @throws RemoteException if something goes wrong in the connection
     */
    void gameIsStarted() throws RemoteException;

    /**
     * Gets all game objects in the game
     * @return a list of game objects currently active
     * @throws RemoteException if something goes wrong in the connection
     */
    List<GameObject> getGameObjects() throws RemoteException;

    /**
     * Gets the set game rules for the currently active game
     * @return a list of gamerule objects which identify the rules of the game
     * @throws RemoteException if something goes wrong in the connection
     */
    List<Gamerule> getGameRules() throws RemoteException;

    /**
     * ends the game and retrieves a list of player objects with a score
     * @return a list of player objects with a score
     * @throws RemoteException if something goes wrong in the connection
     */
    List<PlayerObject> endGame() throws RemoteException;

    /**
     * Moves the specified character in the specified direction
     * @param playerName of the character to be moved
     * @param direction to move the character in
     * @return the moved player object
     * @throws RemoteException if something goes wrong in the connection
     */
    PlayerObject moveCharacter(String playerName, Direction direction) throws RemoteException;

    /**
     * Gets all current players in the game
     * @return a list of player objects which participate in the game
     * @throws RemoteException if something goes wrong in the connection
     */
    List<PlayerObject> returnPlayerObjects() throws RemoteException;

    /**
     * Gets all obstacle objects in the game
     * @return a list of obstacle objects which are located in the game
     * @throws RemoteException if something goes wrong in the connection
     */
    List<ObstacleObject> returnObstacleObjects() throws RemoteException;

    /**
     * Starts the game
     * @throws RemoteException if something goes wrong in the connection
     */
    void startGame() throws RemoteException;
}
