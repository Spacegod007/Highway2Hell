package logic.administration;

import bootstrapper.Main;
import logic.game.Game;
import logic.remote_method_invocation.RMIGameClient;
import logic.remote_method_invocation.RMIGameServer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * SampleMain entry point to start the game server as host
 */
public class HostAdministration implements Runnable
{

    /**
     * The lobby from which the host starts the game
     */
    private final Lobby lobby;

    /**
     * Constructs the game administration object
     * @param lobby from which the game is being started
     */
    HostAdministration(Lobby lobby) throws RemoteException
    {
        this.lobby = lobby;
    }

    /**
     * Runs the host-side of the game in a separate thread
     */
    @Override
    public void run()
    {
        new RMIGameServer(new Game(new ArrayList<>(), lobby.getPlayers()));
    }
}
