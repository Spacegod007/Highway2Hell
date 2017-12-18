package logic.administration;

import logic.fontyspublisher.IRemotePropertyListener;
import logic.remote_method_invocation.RMIGameClient;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Client-side of game administration
 */
public class InGameAdministration  extends UnicastRemoteObject implements IRemotePropertyListener
{
    /**
     * Connection to RMI server
     */
    private RMIGameClient rmiGameClient;

    /**
     * Constructs the client-side of the game administration
     * @param rmiclient connection to the RMI server
     * @throws RemoteException
     */
    public InGameAdministration(RMIGameClient rmiclient) throws RemoteException
    {
        this.rmiGameClient = rmiclient;
    }

    /**
     * Event which gets triggered whenever a property on the server changes
     * @param evt PropertyChangeEvent @see java.beans.PropertyChangeEvent
     * @throws RemoteException when an error occurs in the connection
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        if(evt.getPropertyName().equals("gameState"))
        {

        }
    }
}
