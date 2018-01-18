package logic.remote_method_invocation;

import logic.administration.Lobby;
import logic.administration.User;
import logic.game.CharacterColor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The allowed methods for interaction on the lobbyAdmin
 */
public interface ILobbyAdmin extends Remote
{
    /**
     * Gets the number of lobbies available
     * @return the number of lobbies
     * @throws RemoteException if there is a problem in the connection
     */
    int getNumberOfLobbies() throws RemoteException;

    /**
     * Gets the active lobby of the specified user
     * @param userId who's lobby is requested
     * @return the lobby the user is located in
     * @throws RemoteException if there is a problem in the connection
     */
    Lobby getActiveLobby(int userId) throws RemoteException;

    /**
     * Adds a lobby to the system
     * @param user user that created the lobby (aka host)
     * @param ipAddress of the user who will run the server later
     * @return The newly created lobby
     * @throws RemoteException if there is a problem in the connection
     */
    Lobby addLobby(String lobby, User user, String ipAddress) throws RemoteException;

    /**
     * Gets a list of all the lobbies
     * @return a list of all the lobbies
     * @throws RemoteException if there is a problem in the connection
     */
    List<Lobby> getLobbies() throws RemoteException;

    /**
     * Makes a user join a lobby
     * @param lobby to be joined
     * @param user to join the lobby
     * @return true if the user succeeded joining, false if the user failed to join
     * @throws RemoteException if there is a problem in the connection
     */
    boolean joinLobby(Lobby lobby, User user) throws RemoteException;

    /**
     * Makes a user leave a lobby
     * @param userId of the user who will leave the lobby
     * @param issuerId of the user who made the other user leave the lobby
     * @return true if the user left the lobby, false if leaving the lobby failed
     * @throws RemoteException if there is a problem in the connection
     */
    boolean leaveLobby(int lobby, int userId, int issuerId) throws RemoteException;

    /**
     * Sets the active lobby of the specified user
     * @param userId of the user
     * @param lobby which will be set as active lobby
     * @return the lobby which was set active
     * @throws RemoteException if there is a problem in the connection
     */
    Lobby setActiveLobby(int userId, Lobby lobby) throws RemoteException;

    /**
     * Adds a user to the list of users
     * @param name of the user
     * @return A user object containing the name of the user
     * @throws RemoteException if there is a problem in the connection
     */
    User addUser(String name) throws RemoteException;

    /**
     * Starts the game
     * @param l where the game is being started from
     * @throws RemoteException if there is a problem in the connection
     */
    void startGame(Lobby l) throws RemoteException;

    /**
     * Sets the color of the specified user
     * @param id of the user who's color is being set
     * @param userColor to be set
     * @throws RemoteException if there is a problem in the connection
     */
    void setUserColor(int id, CharacterColor userColor) throws RemoteException;
}
