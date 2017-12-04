package logic.administration;

import logic.remote_method_invocation.RMIGameServer;

public class AdministrationGame implements Runnable
{

    private final Lobby lobby;
    private RMIGameServer server;

    @Override
    public void run()
    {
        server = new RMIGameServer();
    }

    /**
     * Constructor
     * @param lobby
     */
    AdministrationGame(Lobby lobby)
    {
        this.lobby = lobby;
    }
}
