package logic.remote_method_invocation;

import logic.fontyspublisher.IRemotePublisherForDomain;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GameAdmin extends UnicastRemoteObject implements IGameAdmin
{

    /**
     *Remote publisher to publish the host's data to the clients
     */
    private IRemotePublisherForDomain rpd;


    /**
     *
     * @param publisher
     * @throws RemoteException
     */
    public GameAdmin(IRemotePublisherForDomain publisher) throws RemoteException
    {
        this.rpd = publisher;
    }
}
