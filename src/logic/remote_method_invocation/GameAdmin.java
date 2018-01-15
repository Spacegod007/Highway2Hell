package logic.remote_method_invocation;

import logic.Gamerule;
import logic.administration.Lobby;
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
    private final AtomicLong nextSessionId = new AtomicLong(0);

    private final Map<Long, User> sessionMap;

    private final List<User> playerList;
    private Lobby lobby = null;

    private final Object connectSynchronizer;

    public void setLobby(Lobby lobby)
    {
        this.lobby = lobby;
    }

    public int getPlayersConnected()
    {
        return playerList.size();
    }
    public List<User> getPlayerlist()
    {
        return playerList;
    }

    /**
     * Remote publisher to publish the host's data to the clients
     */
    private final IRemotePublisherForDomain rpd;

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
        this.game = game;
        game.setRpd(publisher);
    }

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

    //todo Add game methods
    //todo this class becomes a pipe to the Game object which will live on the Host(server), clients will call remote methods using this and from here the Game class methods will be used
}
