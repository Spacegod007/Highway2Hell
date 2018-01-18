package logic.remote.method.invocation;

import logic.fontyspublisher.RemotePublisher;
import logic.game.Game;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMIGameServer
{
    private static final Logger LOGGER = Logger.getLogger(RMIGameServer.class.getName());

    /**
     * The port number of the game server
     */
    private static final int PORT_NUMBER = 1111;

    /**
     * The binding name of the game server
     */
    private static final String GAME_ADMIN = "gameAdmin";

    /**
     * The binding name of the game publisher
     */
    private static final String BINDING_NAME_PUBLISHER = "publisher";

    /**
     * Constructs the RMI server
     * @param game the game
     */
    public RMIGameServer(Game game)
    {

        // Print port number for registry
        String portNumberMessage = String.format("GameServer: port number %s", PORT_NUMBER);
        LOGGER.log(Level.INFO, portNumberMessage);

        // Create student administration
        /*
      The publisher which informs users of changes on the game server
     */
        RemotePublisher publisher = null;
        /*
      The game administration object where clients interact with
     */
        GameAdmin gameAdmin;
        try
        {
            publisher = new RemotePublisher();
            gameAdmin = new GameAdmin(publisher, game);
            LOGGER.log(Level.INFO, "GameServer: game created");
        }
        catch (RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "GameServer: Cannot create game, Remote exception", ex);
            gameAdmin = null;
        }

        // Create registry at port number
        /*
      The registry where all communication objects are being stored in
     */
        Registry registry;
        try
        {
            registry = LocateRegistry.createRegistry(PORT_NUMBER);
            String registryCreatedMessage = String.format("GameServer: Registry created on port number %s", PORT_NUMBER);
            LOGGER.log(Level.INFO, registryCreatedMessage);
        }
        catch (RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "GameServer: cannot create registry, Remote exception", ex);
            registry = null;
        }

        try
        {
            assert registry != null;
            registry.rebind(BINDING_NAME_PUBLISHER, publisher);
        }
        catch(RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "GameServer: cannot bind publisher, Remote exception", ex);
        }
        catch (NullPointerException e){
            LOGGER.log(Level.SEVERE, "GameServer: cannot bind publisher, Null pointer exception", e);
        }
        // Bind student administration using registry
        try
        {
            registry.rebind(GAME_ADMIN, gameAdmin);
        }
        catch (RemoteException ex)
        {
            LOGGER.log(Level.SEVERE, "GameServer: cannot bind o, remote exception", ex);
        }
    }

}
