package logic.administration;

import logic.fontyspublisher.IRemotePropertyListener;
import logic.remote_method_invocation.RMIGameClient;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class InGameAdministration  extends UnicastRemoteObject implements IRemotePropertyListener
{
    private RMIGameClient rmiGameClient;

    public InGameAdministration(RMIGameClient rmiclient) throws RemoteException
    {
        this.rmiGameClient = rmiclient;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        if(evt.getPropertyName().equals("gameState"))
        {

        }
    }
}
