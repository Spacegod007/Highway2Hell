package logic.remote_method_invocation;

import logic.fontyspublisher.RemotePublisher;
import logic.game.Game;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIGameServer
{

    /**
     * The port number of the game server
     */
    private static final int portNumber = 1111;

    /**
     * The binding name of the game server
     */
    private static final String bindingName = "gameAdmin";

    /**
     * The binding name of the game publisher
     */
    private static final String bindingNamePublisher = "publisher";

    /**
     * The registry where all communication objects are being stored in
     */
    private Registry registry = null;

    /**
     * The game administration object where clients interact with
     */
    private GameAdmin gameAdmin;

    /**
     * The publisher which informs users of changes on the game server
     */
    private RemotePublisher publisher = null;

    /**
     * Constructs the RMI server
     * @param game the game
     */
    public RMIGameServer(Game game)
    {

        // Print port number for registry
        System.out.println("Server: Port number " + portNumber);

        // Create student administration
        try
        {
            publisher = new RemotePublisher();
            gameAdmin = new GameAdmin(publisher, game);
            System.out.println("Server: game created");
        }
        catch (RemoteException ex)
        {
            System.out.println("Server: Cannot create game");
            System.out.println("Server: RemoteException: " + ex.getMessage());
            gameAdmin = null;
        }

        // Create registry at port number
        try
        {
            registry = LocateRegistry.createRegistry(portNumber);
            System.out.println("Server: Registry created on port number " + portNumber);
        }
        catch (RemoteException ex)
        {
            System.out.println("Server: Cannot create registry");
            System.out.println("Server: RemoteException: " + ex.getMessage());
            registry = null;
        }

        try
        {
            registry.rebind(bindingNamePublisher, publisher);
        }
        catch(RemoteException ex)
        {
            System.out.println("Server: Cannot bind publisher");
            System.out.println("Server: RemoteException: " + ex.getMessage());
        }
        catch (NullPointerException e){
            System.out.println("Server: Cannot bind publisher");
            System.out.println("Server: NullpointerException: " + e.getMessage());
        }
        // Bind student administration using registry
        try
        {
            registry.rebind(bindingName, gameAdmin);
        }
        catch (RemoteException ex)
        {
            System.out.println("Server: Cannot bind o");
            System.out.println("Server: RemoteException: " + ex.getMessage());
        }
    }

    /**
     * Print IP addresses and network interfaces
     */
    private static void printIPAddresses()
    {
        try
        {
            InetAddress localhost = InetAddress.getLocalHost();
            System.out.println("GameServer: IP Address: " + localhost.getHostAddress());
            // Just in case this host has multiple IP addresses....
            InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
            if (allMyIps != null && allMyIps.length > 1)
            {
                System.out.println("GameServer: Full list of IP addresses:");

                for (InetAddress allMyIp : allMyIps)
                {
                    System.out.println("    " + allMyIp);
                }
            }
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Server: Cannot get IP address of local host");
            System.out.println("Server: UnknownHostException: " + ex.getMessage());
        }
    }
}
