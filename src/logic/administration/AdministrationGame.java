package logic.administration;

import logic.remote_method_invocation.RMIGameClient;
import logic.remote_method_invocation.RMIGameServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Main entry point to start the game server as host
 */
public class AdministrationGame extends UnicastRemoteObject implements Runnable
{

    /**
     * The lobby from which the host starts the game
     */
    private List<RMIGameClient> clientsConnected;
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
        clientsConnected = new ArrayList<>();
    }


    /**
     * Constructs the game administration object
     * @param lobby from which the game is being started
     */
    AdministrationGame(Lobby lobby) throws RemoteException
    {
        this.lobby = lobby;
    }
}
