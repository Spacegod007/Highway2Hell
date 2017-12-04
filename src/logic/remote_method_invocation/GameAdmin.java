package logic.remote_method_invocation;

import logic.fontyspublisher.IRemotePublisherForDomain;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * the server-side (host) of the game administration
 */
public class GameAdmin extends UnicastRemoteObject implements IGameAdmin
{

    /**
     * Remote publisher to publish the host's data to the clients
     */
    private IRemotePublisherForDomain rpd;


    /**
     * Constructs a GameAdmin object
     * @param publisher to publish the host's data to the clients
     * @throws RemoteException on errors in the connection
     */
    public GameAdmin(IRemotePublisherForDomain publisher) throws RemoteException
    {
        this.rpd = publisher;
    }
}
