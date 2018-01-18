package logic.administration;

import logic.game.Game;
import logic.remote.method.invocation.RMIGameServer;

import java.util.ArrayList;

/**
 * SampleMain entry point to start the game server as host
 */
class HostAdministration implements Runnable
{

    /**
     * The lobby from which the host starts the game
     */
    private final Lobby lobby;

    /**
     * Constructs the game administration object
     * @param lobby from which the game is being started
     */
    HostAdministration(Lobby lobby)
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
