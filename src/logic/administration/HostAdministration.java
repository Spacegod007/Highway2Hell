package logic.administration;

import bootstrapper.Main;
import logic.remote_method_invocation.RMIGameClient;
import logic.remote_method_invocation.RMIGameServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * SampleMain entry point to start the game server as host
 */
public class HostAdministration extends UnicastRemoteObject implements Runnable
{

    /**
     * The game application which runs when the game starts
     */
    Main gameApplication;

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
        server = new RMIGameServer();
        clientsConnected = new ArrayList<>();
    }

    /**
     * Starts the game when the Administration says everyone is connected
     */
    public synchronized void startGame(List<RMIGameClient> clientsConnected)
    {
        this.clientsConnected = clientsConnected;
        System.out.println("Start game");
        gameApplication = new Main();
        for(RMIGameClient client : clientsConnected)
        {
            System.out.println(client.toString());
        }
        //todo fetch stage (or scenes or something like that) from gameApplication

    }
}
