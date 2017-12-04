/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.remote_method_invocation;

import logic.fontyspublisher.IRemotePublisherForListener;
import logic.administration.Lobby;
import logic.administration.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * The client for RMI
 */
public class RMIGameClient
{
    /**
     * The binding name for the administration
     */
    private static final String bindingName = "gameAdmin";

    /**
     * The bindingname for the publisher
     */
    private static final String bindingNamePublisher = "publisher";

    /**
     * References to registry and lobby administration
     */
    private Registry registry = null;

    /**
     * The lobbyadmin which acts as the local lobbyadmin
     */
    private IGameAdmin gameAdmin = null;

    /**
     * The publisher for listener
     */
    private IRemotePublisherForListener remotePublisherForListener;

    /**
     * Gets the publisher for listener
     * @return the publisher for listener
     */
    public IRemotePublisherForListener getRpl()
    {
        return remotePublisherForListener;
    }

    /**
     * Constructs the RMI client by use of specified properties
     * @param ipAddress used to construct the RMI client
     */
    public RMIGameClient(String ipAddress)
    {
        callClient(ipAddress, 1111);
    }

    /**
     * Constructs the RMI client
     * @param ipAddress of the host
     * @param portNumber of the host should he decide to open a lobby
     */
    public RMIGameClient(String ipAddress, int portNumber)
    {
        callClient(ipAddress, portNumber);
    }

    /**
     * used to create and initiate the RMI client
     * @param ipAddress of the host
     * @param portNumber of the server-lobby
     */
    private void callClient(String ipAddress, int portNumber)
    {
        // Print IP address and port number for registry
        System.out.println("GameClient: IP Address: " + ipAddress);
        System.out.println("GameClient: Port number " + portNumber);

        // Locate registry at IP address and port number
        try {
            registry = LocateRegistry.getRegistry(ipAddress, portNumber);
        } catch (RemoteException ex) {
            System.out.println("Client: Cannot locate registry");
            System.out.println("GameClient: RemoteException: " + ex.getMessage());
            registry = null;
        }

        // Print result locating registry
        if (registry != null) {
            System.out.println("Client: Registry located");
        } else {
            System.out.println("Client: Cannot locate registry");
            System.out.println("Client: Registry is null pointer");
        }

        // Print contents of registry
        if (registry != null) {
            printContentsRegistry();
        }

        // Bind student administration using registry
        if (registry != null) {
            try
            {
                gameAdmin = (IGameAdmin) registry.lookup(bindingName);
            }
            catch (RemoteException ex)
            {
                System.out.println("GameClient: Cannot bind lobby administration");
                System.out.println("Client: RemoteException: " + ex.getMessage());
                gameAdmin = null;
            }
            catch (NotBoundException ex)
            {
                System.out.println("Client: Cannot bind lobby administration");
                System.out.println("Client: NotBoundException: " + ex.getMessage());
                gameAdmin = null;
            }
        }

        // Bind publisher using registry
        if (registry != null)
        {
            try
            {
                remotePublisherForListener = (IRemotePublisherForListener) registry.lookup(bindingNamePublisher);
            }
            catch (RemoteException ex)
            {
                System.out.println("GameClient: Cannot bind lobby administration");
                System.out.println("Client: RemoteException: " + ex.getMessage());
                remotePublisherForListener = null;
            }
            catch (NotBoundException ex)
            {
                System.out.println("Client: Cannot bind lobby administration");
                System.out.println("Client: NotBoundException: " + ex.getMessage());
                remotePublisherForListener = null;
            }
        }

        // Print result binding student administration
        if (gameAdmin != null)
        {
            System.out.println("Client: Student administration bound");
        }
        else
        {
            System.out.println("Client: Student administration is null pointer");
        }

        // Test RMI connection
        System.out.println("CONNECTED!");
    }


    /**
     * Print contents of registry
     */
    private void printContentsRegistry()
    {
        try
        {
            String[] listOfNames = registry.list();
            System.out.println("GameClient: list of names bound in registry:");

            if (listOfNames.length != 0)
            {
                for (String s : registry.list())
                {
                    System.out.println(s);
                }
            }
            else
            {
                System.out.println("Client: list of names bound in registry is empty");
            }
        }
        catch (RemoteException ex)
        {
            System.out.println("Client: Cannot show list of names bound in registry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
        }
    }


    /**
     * Test RMI connection
     * !Should implement working test
     */
    // TODO: 4-12-2017 : implement RMItest
    private void testConnection()
    {

    }


    /**
     * Main method to start the RMI client
     * @param args
     */
    @Deprecated
    public static void main(String[] args)
    {
        // Welcome message
        System.out.println("CLIENT USING REGISTRY");

        // Get ip address of server
        Scanner input = new Scanner(System.in);
        System.out.print("Client: Enter IP address of server: ");
        String ipAddress = input.nextLine();

        // Get port number
        System.out.print("Client: Enter port number: ");
        int portNumber = input.nextInt();

        // Create client
        new RMILobbyClient(ipAddress, portNumber);
    }

    /**
     * Gets the connection properties from the properties file
     * @return a properties object containing the connection values
     */
    // TODO: 4-12-2017 Create properties for GameClient
    public static Properties getConnectionProperties()
    {
        Properties properties = new Properties();

        File file = new File("properties/lobbyAdmin.properties");
        try (InputStream inputStream = new FileInputStream(file))
        {
            properties.load(inputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return properties;
    }
}
