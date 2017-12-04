package logic.administration;

import logic.remote_method_invocation.RMIGameServer;

/**
 * Main entry point to start the game server as host
 */
public class AdministrationGame implements Runnable
{

    /**
     * The lobby from which the host starts the game
     */
    private final Lobby lobby;

    /**
     * The RMI connector where clients connect to
     */
    private RMIGameServer server;

    /**
     * Runs the host-side of the game in a separate thread
     */
    @Override
    public void run()
    {
        server = new RMIGameServer();
    }

    /**
     * Constructs the game administration object
     * @param lobby from which the game is being started
     */
    AdministrationGame(Lobby lobby)
    {
        this.lobby = lobby;
    }
}
