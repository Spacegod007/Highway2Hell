package logic.remote_method_invocation;

import logic.Gamerule;
import logic.administration.User;
import logic.fontyspublisher.IRemotePublisherForDomain;
import logic.game.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * the server-side (host) of the game administration
 */
public class GameAdmin extends UnicastRemoteObject implements IGameAdmin
{
    /**
     * The next available session id for clients
     */
    private final AtomicLong nextSessionId = new AtomicLong(0);

    /**
     * A mapping of the currently active session ids
     */
    private final Map<Long, User> sessionMap;

    /**
     * A list of participating players
     */
    private final List<User> playerList;

    /**
     * A synchronise object to synchronise the connection over multiple threads
     */
    private final Object connectSynchronizer;

    /**
     * Gets the amount of players currently connected
     * @return an integer value containing the amount of connected players
     */
    public int getPlayersConnected()
    {
        return playerList.size();
    }

    /**
     * Gets the playerlist
     * @return a list of user objects
     */
    public List<User> getPlayerlist()
    {
        return playerList;
    }

    /**
     * Remote publisher to publish the host's data to the clients
     */
    private final IRemotePublisherForDomain rpd;

    /**
     * The currently being played game
     */
    private Game game;

    /**
     * Constructs a GameAdmin object
     * @param publisher to publish the host's data to the clients
     * @throws RemoteException on errors in the connection
     */
    public GameAdmin(IRemotePublisherForDomain publisher, Game game) throws RemoteException
    {
        connectSynchronizer = new Object();
        playerList = new ArrayList<>();
        sessionMap = new HashMap<>();
        this.rpd = publisher;
        rpd.registerProperty("playersconnected");
        rpd.registerProperty("gameIsStarted");
        rpd.registerProperty("gameObjects");
        rpd.registerProperty("gameRules");
        rpd.registerProperty("allDead");
        this.game = game;
        game.setRpd(publisher);
    }

    /**
     * The method which connects a client to the game
     * @param client that connected to the game
     * @return the sessionId linked to this client
     */
    public long connect(User client)
    {
        synchronized (connectSynchronizer)
        {
            sessionMap.put(nextSessionId.incrementAndGet(), client);
            playerList.add(client);
            try
            {
                rpd.inform("playersconnected", null, playerList.size());
            } catch (RemoteException e)
            {
                e.printStackTrace();
            }
            return nextSessionId.get();
        }
    }

    /**
     * Informs the clients the game has started
     */
    public void gameIsStarted()
    {
        try
        {
            rpd.inform("gameIsStarted", null, playerList);
            rpd.registerProperty("gameState");
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public List<GameObject> getGameObjects()
    {
        return game.getGameObjects();
    }

    @Override
    public List<Gamerule> getGameRules()
    {
        return game.getGamerules();
    }

    @Override
    public List<PlayerObject> endGame()
    {
        return game.endGame();
    }

    @Override
    public PlayerObject moveCharacter(String playerName, Direction direction)
    {
        return game.moveCharacter(playerName, direction);
    }

    @Override
    public List<PlayerObject> returnPlayerObjects()
    {
        return game.returnPlayerObjects();
    }

    @Override
    public List<ObstacleObject> returnObstacleObjects()
    {
        return game.returnObstacleObjects();
    }

    @Override
    public void startGame()
    {
        game.startGame();
    }
}
